package ca.louie;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Theme(value = Lumo.class, variant = Lumo.DARK)
@CssImport(value = "./styles/spotify-theme.css")
@CssImport(value = "./styles/text-field.css", themeFor = "vaadin-text-field")
@CssImport(value = "./styles/navbar.css", themeFor = "vaadin-app-layout")
@CssImport(value = "./styles/tabs.css", themeFor = "vaadin-tabs")
@CssImport(value = "./styles/tab.css", themeFor = "vaadin-tab")
public class MainView extends AppLayout implements BeforeEnterObserver {

	private Tabs tabs = new Tabs();
	private Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();

	public MainView() {
		Image logo = new Image("https://i.imgur.com/eRhZbPi.png", "six degrees logo");
		logo.setHeight("20px");
		logo.getElement().getStyle().set("padding-left", "16px");
		addMenuTab("search", SearchView.class);
		addMenuTab("about", AboutView.class);
		tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
		tabs.getElement().getStyle().set("margin-left", "auto");
		addToNavbar(logo, tabs);
	}

	private void addMenuTab(String label, Class<? extends Component> target) {
		Tab tab = new Tab(new RouterLink(label, target));
		navigationTargetToTab.put(target, tab);
		tabs.add(tab);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		tabs.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));

	}

}
