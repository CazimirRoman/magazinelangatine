package magazinelangatine.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MarkerJSONParser {

	/** Receives a JSONObject and returns a list */
	public List<HashMap<String, String>> parse(JSONObject jObject) {

		JSONArray jMarkers = null;
		try {
			/** Retrieves all the elements in the 'response' array */
			jMarkers = jObject.getJSONArray("listamagazine");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		/**
		 * Invoking getMarkers with the array of json object where each json
		 * object represent a marker
		 */
		return getMarkers(jMarkers);
	}

	private List<HashMap<String, String>> getMarkers(JSONArray jMarkers) {
		int markersCount = jMarkers.length();
		List<HashMap<String, String>> markersList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> marker = null;

		/** Taking each marker, parses and adds to list object */
		for (int i = 0; i < markersCount; i++) {
			try {
				/** Call getMarker with marker JSON object to parse the marker */
				marker = getMarker((JSONObject) jMarkers.get(i));
				markersList.add(marker);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return markersList;
	}

	/** Parsing the Marker JSON object PARSAREA EFECTIVA */
	private HashMap<String, String> getMarker(JSONObject jMarker) {

		HashMap<String, String> marker = new HashMap<String, String>();

		// sunt puse initial pe NA ca sa nu dea NPE daca nu apare campul in
		// JSON.

		String id = "-NA-";
		String name = "-NA-";
		String lat = "-NA-";
		String lng = "-NA-";
		String tipmagsel = "-NA-";
		String descmag = "-NA-";
		String nonstopflag = "-NA-";

		try {
			// Extracting ID
			if (!jMarker.isNull("id")) {
				id = jMarker.getString("id");
			}

			// Extracting name, if available
			if (!jMarker.isNull("name")) {
				name = jMarker.getString("name");
			}

			// Extracting latitude, if available
			if (!jMarker.isNull("lat")) {
				lat = jMarker.getString("lat");
			}

			// Extracting longitude, if available
			if (!jMarker.isNull("lng")) {
				lng = jMarker.getString("lng");
			}
			// Extracting type, if available
			if (!jMarker.isNull("tip")) {
				tipmagsel = jMarker.getString("tip");
			}
			// Extracting desc, if available
			if (!jMarker.isNull("desc")) {
				descmag = jMarker.getString("desc");
			}

			// Extracting nonstopflag, if available
			if (!jMarker.isNull("nonstop")) {
				nonstopflag = jMarker.getString("nonstop");
			}

			marker.put("id", id);
			marker.put("name", name);
			marker.put("lat", lat);
			marker.put("lng", lng);
			marker.put("tip", tipmagsel);
			marker.put("desc", descmag);
			marker.put("nonstop", nonstopflag);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return marker;
	}
}
