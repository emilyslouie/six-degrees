package ca.louie;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;

import ca.louie.spotify.data.Artist;
import ca.louie.spotify.data.ArtistRepository;
import ca.louie.spotify.data.Track;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Route(value = "", layout = MainView.class)
public class SearchView extends Div {

	private final ArtistRepository repo;
	private Set<String> songs = new HashSet<String>();
	private boolean searchedAlready = false;
	private Sort sort = Sort.by("popularity").descending();

	public boolean isMobileDevice() {
		WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
		return webBrowser.isAndroid() || webBrowser.isIPhone() || webBrowser.isWindowsPhone();
	}

	public SearchView(ArtistRepository repo) {
		// ui for the search
		Div footerText = new Div();
		footerText.add(new Text("created by "), new Anchor("https://emily.louie.ca", "emily louie"),
				new Text(" using the "), new Anchor("https://developer.spotify.com", "spotify api"),
				new Text(" and java sping boot and vaadin."));
		footerText.getElement().getStyle().set("text-align", "center");
		footerText.getElement().getStyle().set("padding", "50px");
		footerText.getElement().getStyle().set("padding-top", "100px");
		footerText.getElement().getStyle().set("color", "#a5a5a5");
		H2 rhetQuestion = new H2("are these artists connected within six degrees?");
		H2 rhetAnswer = new H2("--> let's find out.");
		rhetAnswer.getElement().getStyle().set("text-align", "right");
		rhetAnswer.getElement().getStyle().set("padding-bottom", "50px");
		rhetAnswer.setWidthFull();
		if (!isMobileDevice()) {
			rhetQuestion.getElement().getStyle().set("border-bottom", "2px solid hsl(214, 90%, 52%)");
			rhetQuestion.getElement().getStyle().set("display", "inline-block");
		} else {
			rhetQuestion.getElement().getStyle().set("border-left", "2px solid hsl(214, 90%, 52%)");
			rhetQuestion.getElement().getStyle().set("padding-left", "7px");
		}

		this.repo = repo;
		TextField artist1Field = new TextField();
		artist1Field.setLabel("first artist");
		artist1Field.setPlaceholder("ex. billie eilish");
		TextField artist2Field = new TextField();
		artist2Field.setLabel("second artist");
		artist2Field.setPlaceholder("ex. ed sheeran");
		Button searchButton = new Button("search");
		searchButton.getElement().getThemeList().add("primary");
		VerticalLayout homeElements = new VerticalLayout();
		Text searchLabel = new Text("enter the two artists you want to search for:");

		if (!isMobileDevice()) {
			HorizontalLayout search = new HorizontalLayout();
			search.setDefaultVerticalComponentAlignment(Alignment.END);
			search.addAndExpand(artist1Field, artist2Field, searchButton);
			homeElements.add(rhetQuestion, rhetAnswer, searchLabel, search);

		} else {
			artist2Field.getElement().getStyle().set("padding-top", "10px");
			artist2Field.getElement().getStyle().set("padding-bottom", "20px");
			homeElements.add(rhetQuestion, rhetAnswer, searchLabel, artist1Field, artist2Field, searchButton);
			homeElements.getElement().getStyle().set("padding-left", "32px");
		}
		homeElements.getElement().getStyle().set("margin", "auto");
		homeElements.getElement().getStyle().set("padding-top", "50px");
		homeElements.setWidth("auto");
		HorizontalLayout homeContainer = new HorizontalLayout();
		homeContainer.setWidthFull();
		homeContainer.setHeightFull();
		homeContainer.add(homeElements);
		add(homeContainer, footerText);
		searchButton.addClickShortcut(Key.ENTER);

		VerticalLayout resultsContainer = new VerticalLayout();

		Button searchAgainButton = new Button("search again");
		searchAgainButton.getElement().getThemeList().add("primary");
		searchAgainButton.getElement().getStyle().set("margin", "auto");

		searchButton.addClickListener((e -> {
			// generates the ui for the result
			homeContainer.setVisible(false);
			songs.clear();
			String firstArtist = artist1Field.getValue();
			String secondArtist = artist2Field.getValue();
			List<Artist> resultsArtist1 = repo.findByNameContainingIgnoreCaseOrderByPopularityDesc(firstArtist, sort);
			List<Artist> resultsArtist2 = repo.findByNameContainingIgnoreCaseOrderByPopularityDesc(secondArtist, sort);
			Artist artist1DB = new Artist();
			Artist artist2DB = new Artist();

			H2 resultsHeader = new H2();
			resultsHeader.getElement().getStyle().set("text-align", "center");
			VerticalLayout resultsLayout = new VerticalLayout();
			resultsLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
			VerticalLayout artistPath = new VerticalLayout();

			if (resultsArtist1.size() != 0 && resultsArtist2.size() != 0) {
				artist1DB = resultsArtist1.get(0);
				log.info("artist1: " + artist1DB);
				artist2DB = resultsArtist2.get(0);
				log.info("artist2" + artist2DB);
				List<Artist> path = searchArtists(artist1DB, artist2DB);
				String artist1Name = artist1DB.getName().toLowerCase();
				String artist2Name = artist2DB.getName().toLowerCase();
				resultsHeader = new H2("see how " + artist1Name + " and " + artist2Name + " are connected.");
				artistPath = pathUI(path);
			} else {
				resultsHeader = new H2("one or more of the requested artists are not found.");
				artistPath = incompleteUI();
			}
			resultsLayout.add(resultsHeader, artistPath);
			resultsLayout.getElement().getStyle().set("padding-bottom", "50px");
			resultsContainer.add(resultsLayout, searchAgainButton);
			if (!searchedAlready) {
				add(resultsContainer, footerText);
				searchedAlready = true;
			} else {
				resultsContainer.setVisible(true);
			}

		}));

		searchAgainButton.addClickListener((e -> {
			resultsContainer.setVisible(false);
			resultsContainer.removeAll();
			artist1Field.clear();
			artist2Field.clear();
			homeContainer.setVisible(true);
		}));

	}

	List<Artist> searchArtists(Artist firstArtist, Artist secondArtist) {
		List<Artist> path = repo.findShortestPath(firstArtist.getSpotifyId(), secondArtist.getSpotifyId());
		return path;
	}

	VerticalLayout pathUI(List<Artist> path) {
		VerticalLayout artistPath = new VerticalLayout();
		artistPath.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		if (path.size() == 0) {
			H1 subheader = new H1("no connections found.");
			subheader.getElement().getStyle().set("text-align", "center");
			Div contactText = new Div();
			contactText.add(new Text("is this a mistake? contact me "),
					new Anchor("mailto:emilyslouie@gmail.com", "here"), new Text("."));
			contactText.getElement().getStyle().set("text-align", "center");
			artistPath.add(subheader, contactText);
		} else {
			for (int i = 0; i < path.size(); i++) {
				if (i == path.size() - 1) {
					H1 artistName = new H1(path.get(i).getName().toLowerCase());
					artistName.getElement().getStyle().set("text-align", "center");
					artistPath.add(artistName);
				} else {
					artistPath = generateArtist(path.get(i), artistPath);
				}
			}
		}
		return artistPath;
	}

	VerticalLayout incompleteUI() {
		VerticalLayout errorPath = new VerticalLayout();
		errorPath.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		H1 subheader = new H1("please try searching again with valid artist names.");
		subheader.getElement().getStyle().set("text-align", "center");
		Div contactText = new Div();
		contactText.add(new Text("is this a mistake? contact me "), new Anchor("mailto:emilyslouie@gmail.com", "here"),
				new Text("."));
		contactText.getElement().getStyle().set("text-align", "center");
		errorPath.add(subheader, contactText);
		return errorPath;
	}

	VerticalLayout generateArtist(Artist artist, VerticalLayout layout) {
		H1 header = new H1(artist.getName().toLowerCase());
		header.getElement().getStyle().set("text-align", "center");
		String connectingTrack = "";
		for (Iterator iterator = artist.getTracks().iterator(); iterator.hasNext();) {
			Track track = (Track) iterator.next();
			connectingTrack = track.getExternalUrl();
			if (!songs.contains(connectingTrack)) {
				songs.add(connectingTrack);
				break;
			}
		}
		// manual html done for spotify embed because embedding a song requires embed in
		// the url and the vaadin iframe does not work
		String url = "<iframe src=\"" + connectingTrack
				+ "\" width=\"300\" height=\"80\" frameborder=\"0\" allowtransparency=\"true\" allow=\"encrypted-media\"></iframe>";
		url = url.replace("track", "embed/track");
		Html songPreview = new Html(url);
		layout.add(header, songPreview);
		return layout;
	}

}
