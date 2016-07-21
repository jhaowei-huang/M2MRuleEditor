package iot.ttu.edu.M2MRuleEditor;

import javax.servlet.annotation.WebServlet;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import iot.ttu.edu.M2MRuleEditor.coap.M2MCoapClient;
import iot.ttu.edu.M2MRuleEditor.ui.Editor;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Push
@Theme("theme")
@Widgetset("iot.ttu.edu.M2MRuleEditor.RuleEditorWidgetset")
public class RuleEditorUI extends UI {
	private Editor layout;
	String responseText = "";

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		RuleEditorUI.this.setImmediate(true);

		M2MCoapClient.connect();
		M2MCoapClient.client.get(new CoapHandler() {

			@Override
			public void onError() {
				System.out.println("coap get on error");
				new EditorRefreshThread(false).start();
			}

			@Override
			public void onLoad(CoapResponse response) {
				System.out.println("coap get on load = " + response.getResponseText());
				String responseText = response.getResponseText();
				layout = new Editor(responseText.split(","));
				new EditorRefreshThread(true).start();
			}
		});
	}

	class EditorRefreshThread extends Thread {
		private boolean state = false;

		public EditorRefreshThread(boolean state) {
			this.state = state;
		}

		@Override
		public void run() {
			if (state) {
				RuleEditorUI.this.access(() -> RuleEditorUI.this.setContent(layout));
			} else {
				final Window window = new Window("Connect Gateway Error!");
				window.setWidth(300.0f, Unit.PIXELS);
				window.setHeight(300.0f, Unit.PIXELS);
				window.setDraggable(false);
				window.setContent(new Label("error !!"));
				UI.getCurrent().addWindow(window);
				window.center();
				window.focus();
				window.setModal(true);
			}
		}
	}

	@WebServlet(urlPatterns = "/*", name = "RuleEditorUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = RuleEditorUI.class, productionMode = false)
	public static class RuleEditorUIServlet extends VaadinServlet {
	}
}
