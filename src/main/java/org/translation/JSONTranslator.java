package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final JSONArray jsonArray;
    private final Map codeToLangs;

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            jsonArray = new JSONArray(jsonString);
            codeToLangs = new HashMap();
            int i = 0;
            int len = jsonArray.length();
            while (i < len) {
                JSONObject country = jsonArray.getJSONObject(i);
                List<String> keySet = new ArrayList<String>(country.keySet());
                Iterator<String> it = keySet.iterator();
                Map countryLangs = new HashMap();
                while (it.hasNext()) {
                    String lang = it.next();
                    if (!"id".equals(lang) && !"alpha3".equals(lang) && !"alpha2".equals(lang)) {
                        countryLangs.put(lang, country.getString(lang));
                    }
                }
                codeToLangs.put(country.getString("alpha3"), countryLangs);
                i++;
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        Map langs = (Map) codeToLangs.get(country);
        List<String> l = new ArrayList<String>(langs.keySet());
        return l;
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<String>(codeToLangs.keySet());
    }

    @Override
    public String translate(String country, String language) {
        Map translations = (Map) codeToLangs.get(country);
        return (String) translations.get(language);
    }
}
