package TestJackson;

import org.immo.exceptions.UnknownResponseCode;
import org.immo.geojson.adresseban.AdresseBAN;
import org.immo.geojson.feuille.Feuille;
import org.immo.geojson.geomutation.Geomutation;
import org.immo.geojson.mutation.Mutation;
import org.immo.geojson.parcelle.Parcelle;
import org.immo.model.ResponseManagerHTTP;
import org.immo.servicepublicapi.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ResponseManagerHTTPTest {

    @Test
    void controleAdresseRetourMultiples() throws IOException, URISyntaxException, UnknownResponseCode {
        String queryAdresse = "202 avenue du Maine";
        AdresseAPI adresse = new AdresseAPI(queryAdresse);
        ResponseManagerHTTP<AdresseBAN> gestionCodeRetour = new ResponseManagerHTTP<>();
        Optional<AdresseBAN> optionalAdresseBAN = gestionCodeRetour.getAPIReturn(adresse, AdresseBAN.class);
        assertTrue(optionalAdresseBAN.isPresent());
        assertEquals(5, optionalAdresseBAN.get().getFeatures().size());
    }

    @Test
    void controleAdresseRetourUnique() throws IOException, URISyntaxException, UnknownResponseCode {
        String queryAdresse = "31 avenue du Bas Meudon";
        AdresseAPI adresse = new AdresseAPI(queryAdresse);
        ResponseManagerHTTP<AdresseBAN> gestionCodeRetour = new ResponseManagerHTTP<>();
        Optional<AdresseBAN> optionalAdresseBAN = gestionCodeRetour.getAPIReturn(adresse, AdresseBAN.class);
        assertFalse(optionalAdresseBAN.isEmpty());
        assertEquals(1, optionalAdresseBAN.get().getFeatures().size());
    }

    @Test
    void controleAdresseErreur400() throws IOException, URISyntaxException, UnknownResponseCode{
        String queryAdresse = "&é\"&é\"&z";
        AdresseAPI adresse = new AdresseAPI(queryAdresse);
        ResponseManagerHTTP<AdresseBAN> gestionCodeRetour = new ResponseManagerHTTP<>();
        assertEquals(400, adresse.getConn().getResponseCode());
        assertThrows(NoSuchElementException.class, () -> gestionCodeRetour.getAPIReturn(adresse, AdresseBAN.class).get());
    }

    @Test
    void controleParcelleRetourUnique() throws IOException, URISyntaxException, UnknownResponseCode {
        String queryAdresse = "202 avenue du Maine";
        AdresseAPI adresse = new AdresseAPI(queryAdresse);
        ResponseManagerHTTP<AdresseBAN> gestionCodeRetour = new ResponseManagerHTTP<>();
        AdresseBAN newAdress = gestionCodeRetour.getAPIReturn(adresse, AdresseBAN.class).get();
        String pointQuery = newAdress.getFeatures().get(0).getGeometry().toString();
        ParcelleAPI newParcelleQuery = new ParcelleAPI(pointQuery,"geom");
        ResponseManagerHTTP<Parcelle> parcelleResponseManagerHTTP = new ResponseManagerHTTP<>();
        Optional<Parcelle> optionalParcelle = parcelleResponseManagerHTTP.getAPIReturn(newParcelleQuery, Parcelle.class);
        assertTrue(optionalParcelle.isPresent());
        assertEquals(1, optionalParcelle.get().getNumberReturned());
    }

    @Test
    void controleParcelleRetourMultiples() throws IOException, URISyntaxException, UnknownResponseCode {
        ParcelleAPI newParcelleQuery = new ParcelleAPI("CJ","section");
        ResponseManagerHTTP<Parcelle> gestionCodeRetour = new ResponseManagerHTTP<>();
        Parcelle newParcelle = gestionCodeRetour.getAPIReturn(newParcelleQuery, Parcelle.class).get();
        assertTrue(newParcelle.getNumberReturned()>1);
    }

    @Test
    void controleParcelleRetourVide() throws IOException, URISyntaxException, UnknownResponseCode {
        ParcelleAPI newParcelleQuery = new ParcelleAPI("##","section");
        ResponseManagerHTTP<Parcelle> gestionCodeRetour = new ResponseManagerHTTP<>();
        Optional<Parcelle> newParcelle = gestionCodeRetour.getAPIReturn(newParcelleQuery, Parcelle.class);
        assertTrue(newParcelle.isEmpty());
    }

    @Test
    void controleGeomutationRetourMultiples () throws IOException, URISyntaxException, UnknownResponseCode {
        String queryAdresse = "202 avenue du Maine";
        AdresseAPI adresse = new AdresseAPI(queryAdresse);
        ResponseManagerHTTP<AdresseBAN> gestionCodeRetour = new ResponseManagerHTTP<>();
        AdresseBAN adresseBAN = gestionCodeRetour.getAPIReturn(adresse, AdresseBAN.class).get();
        String pointQuery = adresseBAN.getFeatures().get(0).getGeometry().toString();
        ParcelleAPI newParcelleQuery = new ParcelleAPI(pointQuery,"geom");

        ResponseManagerHTTP<Parcelle> parcelleResponseManagerHTTP = new ResponseManagerHTTP<>();
        Parcelle newParcelle = parcelleResponseManagerHTTP.getAPIReturn(newParcelleQuery, Parcelle.class).get();
        String bbox = newParcelle.convertBboxToString();

        GeomutationAPI geomutationAPI = new GeomutationAPI("2017", "75114", bbox);

        ResponseManagerHTTP<Geomutation> geomutationResponseManagerHTTP = new ResponseManagerHTTP<>();
        Geomutation newGeomutation = geomutationResponseManagerHTTP.getAPIReturn(geomutationAPI, Geomutation.class).get();
        assertTrue(newGeomutation.getCount()>1);
    }

    //Le test sera KO lorsque la vente de septembre 2023 sera intégrée
    @Test
    void controleGeomutationRetourVide() throws IOException, URISyntaxException, UnknownResponseCode {
        String queryAdresse = "31 avenue du Bas Meudon";
        AdresseAPI adresse = new AdresseAPI(queryAdresse);
        ResponseManagerHTTP<AdresseBAN> gestionCodeRetour = new ResponseManagerHTTP<>();
        AdresseBAN adresseBAN = gestionCodeRetour.getAPIReturn(adresse, AdresseBAN.class).get();
        String pointQuery = adresseBAN.getFeatures().get(0).getGeometry().toString();
        ParcelleAPI newParcelleQuery = new ParcelleAPI(pointQuery,"geom");

        ResponseManagerHTTP<Parcelle> parcelleResponseManagerHTTP = new ResponseManagerHTTP<>();
        Parcelle newParcelle = parcelleResponseManagerHTTP.getAPIReturn(newParcelleQuery, Parcelle.class).get();
        String bbox = newParcelle.convertBboxToString();

        ResponseManagerHTTP<Geomutation> geomutationResponseManagerHTTP = new ResponseManagerHTTP<>();
        GeomutationAPI geomutationAPI = new GeomutationAPI("2023", pointQuery, "92040", bbox);
        assertTrue(geomutationResponseManagerHTTP.getAPIReturn(geomutationAPI, Geomutation.class).isPresent());
    }

    @Test
    void controleMutationRetour () throws IOException, URISyntaxException, UnknownResponseCode {
        MutationAPI mutationAPI = new MutationAPI(9957202);
        ResponseManagerHTTP<Mutation> gestionCodeRetour = new ResponseManagerHTTP<>();

        Mutation mutation = gestionCodeRetour.getAPIReturn(mutationAPI, Mutation.class).get();
        mutation.showMutationContent();
    }

    @Test
    void controleMutationRetourVide () throws IOException, URISyntaxException, UnknownResponseCode {
        MutationAPI mutationAPI = new MutationAPI(0);
        ResponseManagerHTTP<Mutation> gestionCodeRetour = new ResponseManagerHTTP<>();
        assertThrows(NoSuchElementException.class, () -> gestionCodeRetour.getAPIReturn(mutationAPI, Mutation.class).get());
        assertEquals(404, mutationAPI.getConn().getResponseCode());
    }

    @Test
    void controleFeuilleRetourMultiples () throws IOException, URISyntaxException, UnknownResponseCode {
        String query = "{     \"type\": \"Point\",     \"coordinates\": [      2.247021,      48.822554     ]    }";
        FeuilleAPI feuilleAPI = new FeuilleAPI(query, "geom");
        ResponseManagerHTTP<Feuille> gestionCodeRetour = new ResponseManagerHTTP<>();
        Optional<Feuille> optionalFeuille = gestionCodeRetour.getAPIReturn(feuilleAPI, Feuille.class);
        assertEquals(200, feuilleAPI.getConn().getResponseCode());
        assertTrue(optionalFeuille.isPresent());
        assertEquals("urn:ogc:def:crs:EPSG::4326", optionalFeuille.get().getCrs().getCrsProperties().getName());
    }

    @Test
    void checkReturn () throws IOException, URISyntaxException, UnknownResponseCode {
        String query = "{     \"type\": \"Point\",     \"coordinates\": [      2.247021,      48.822554     ]    }";
        FeuilleAPI feuilleAPI = new FeuilleAPI(query, "geom");
        ResponseManagerHTTP<Feuille> gestionCodeRetour = new ResponseManagerHTTP<>();
        Optional<Feuille> optionalFeuille = gestionCodeRetour.getAPIReturn(feuilleAPI, Feuille.class);
        System.out.println(optionalFeuille.get().showTerrainContent());
        assertEquals(200, feuilleAPI.getConn().getResponseCode());
        assertTrue(optionalFeuille.isPresent());
        assertEquals("urn:ogc:def:crs:EPSG::4326", optionalFeuille.get().getCrs().getCrsProperties().getName());
    }
}
