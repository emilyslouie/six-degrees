package ca.louie;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "about", layout = MainView.class)
public class AboutView extends Div {

	public AboutView() {
		VerticalLayout aboutContainer = new VerticalLayout();
		aboutContainer.getElement().getStyle().set("max-width", "630px");
		aboutContainer.getElement().getStyle().set("margin", "auto");
		aboutContainer.getElement().getStyle().set("padding-top", "50px");

		Div footerText = new Div();
		footerText.add(new Text("created by "), new Anchor("https://emily.louie.ca", "emily louie"),
				new Text(" using the "), new Anchor("https://developer.spotify.com", "spotify api"),
				new Text(" and java sping boot and vaadin."));
		footerText.getElement().getStyle().set("text-align", "center");
		footerText.getElement().getStyle().set("padding", "50px");
		footerText.getElement().getStyle().set("padding-top", "100px");
		footerText.getElement().getStyle().set("color", "#a5a5a5");
		H2 definition = new H2("definition: ");
		definition.getElement().getStyle().set("margin-bottom", "-10px");
		Div definitionText = new Div(new Text(
				"the six degrees of separation is the theory that any person on the planet can be connected to any other person on the planet through a chain of acquaintances that has no more than five intermediaries."));
		definitionText.getElement().getStyle().set("font-style", "italic");
		definitionText.getElement().getStyle().set("padding-bottom", "40px");
		Div paragraph1 = new Div(new Text(
				"Six Degrees is a website that demonstrates that a musician is connected to almost every other musician through a chain of collaborations, typically with less than 5 artists in between two of them. "));
		Div paragraph2 = new Div(new Text(
				"From Paul McCartney to BLACKPINK to Ed Sheeran, this web of connections can be explored by searching for two artists."));
		Div paragraph3 = new Div(new Text("This website was inspired by the title of The Script's "),
				new Anchor("https://open.spotify.com/track/0Sayb1EWTywPttwJo7zjBt?si=VoxuS6V7TtO5gZ7C6yN45Q",
						"Six Degrees of Separation"),
				new Text("."));
		String url = "<iframe src=\"https://open.spotify.com/embed/track/0Sayb1EWTywPttwJo7zjBt\" width=\"300\" height=\"80\" frameborder=\"0\" allowtransparency=\"true\" allow=\"encrypted-media\"></iframe>";
		Html songPreview = new Html(url);
		songPreview.getElement().getStyle().set("margin", "auto");
		songPreview.getElement().getStyle().set("padding-top", "25px");
		songPreview.getElement().getStyle().set("padding-bottom", "15px");
		Div paragraph4 = new Div(new Text("The data used for this site is from "),
				new Anchor("https://developer.spotify.com/", "Spotify's API"),
				new Text(" and gathered as of July 2020. It was created using Java Spring Boot, Vaadin, and Neo4j."));
		Div paragraph5 = new Div(new Text("Do you have any feedback or see a mistake? "),
				new Anchor("mailto:emilyslouie@gmail.com", "Contact me."));
		aboutContainer.add(definition, definitionText, paragraph1, paragraph2, paragraph3, songPreview, paragraph4,
				paragraph5);
		add(aboutContainer, footerText);
	}
}
