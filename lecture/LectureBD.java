import java.io.FileInputStream;
import java.io.IOException;

import java.io.InputStream;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class LectureBD {   

   private java.sql.Connection connection;

   
   public class Role {
      public Role(int i, String n, String p) {
         id = i;
         nom = n;
         personnage = p;
      }
      protected int id;
      protected String nom;
      protected String personnage;
   }
   
   public LectureBD() {
      connectionBD();                     
   }
   
   
   public void lecturePersonnes(String nomFichier){   
        System.out.println("Début de lecturePersonnes avec le fichier : " + nomFichier);
   
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

         InputStream is = new FileInputStream(nomFichier);
         parser.setInput(is, null);

         int eventType = parser.getEventType();

         String tag = null, 
                nom = null,
                prenom = null,
                anniversaire = null,
                lieu = null,
                photo = null,
                bio = null;
         
         int id = -1;
         
         while (eventType != XmlPullParser.END_DOCUMENT) 
         {
            if(eventType == XmlPullParser.START_TAG) 
            {
               tag = parser.getName();
               
               if (tag.equals("personne") && parser.getAttributeCount() == 1)
                  id = Integer.parseInt(parser.getAttributeValue(0));
            } 
            else if (eventType == XmlPullParser.END_TAG) 
            {                              
               tag = null;
               
               if (parser.getName().equals("personne") && id >= 0)
               {
                  insertionPersonne(id,nom,prenom, anniversaire,lieu,photo,bio);
                                    
                  id = -1;
                  nom = null;
                  prenom = null;
                  anniversaire = null;
                  lieu = null;
                  photo = null;
                  bio = null;
               }
            }
            else if (eventType == XmlPullParser.TEXT && id >= 0) 
            {
               if (tag != null)
               {                                    
                  if (tag.equals("nom")) {
                    String nomComplet = parser.getText();
                    String[] nomPrenom = nomComplet.split(" ", 2); 
                  if (nomPrenom.length == 2) {
                      prenom = nomPrenom[0];
                      nom = nomPrenom[1];
                  } else {
                      prenom = ""; 
                      nom = nomPrenom[0];
                    }
                  }
                  else if (tag.equals("anniversaire"))
                     anniversaire = parser.getText();
                  else if (tag.equals("lieu"))
                     lieu = parser.getText();
                  else if (tag.equals("photo"))
                     photo = parser.getText();
                  else if (tag.equals("bio"))
                     bio = parser.getText();
               }              
            }
            
            eventType = parser.next();            
         }
      }
      catch (XmlPullParserException e) {
          System.out.println(e);   
       }
       catch (IOException e) {
         System.out.println("IOException while parsing " + nomFichier); 
       }
   }   
   
   public void lectureFilms(String nomFichier){
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

         InputStream is = new FileInputStream(nomFichier);
         parser.setInput(is, null);

         int eventType = parser.getEventType();

         String tag = null, 
                titre = null,
                langue = null,
                poster = null,
                roleNom = null,
                rolePersonnage = null,
                realisateurNom = null,
                resume = null;
         
         ArrayList<String> pays = new ArrayList<String>();
         ArrayList<String> genres = new ArrayList<String>();
         ArrayList<String> scenaristes = new ArrayList<String>();
         ArrayList<Role> roles = new ArrayList<Role>();         
         ArrayList<String> annonces = new ArrayList<String>();
         
         int id = -1,
             annee = -1,
             duree = -1,
             roleId = -1,
             realisateurId = -1;
         
         while (eventType != XmlPullParser.END_DOCUMENT) 
         {
            if(eventType == XmlPullParser.START_TAG) 
            {
               tag = parser.getName();
               
               if (tag.equals("film") && parser.getAttributeCount() == 1)
                  id = Integer.parseInt(parser.getAttributeValue(0));
               else if (tag.equals("realisateur") && parser.getAttributeCount() == 1)
                  realisateurId = Integer.parseInt(parser.getAttributeValue(0));
               else if (tag.equals("acteur") && parser.getAttributeCount() == 1)
                  roleId = Integer.parseInt(parser.getAttributeValue(0));
            } 
            else if (eventType == XmlPullParser.END_TAG) 
            {                              
               tag = null;
               
               if (parser.getName().equals("film") && id >= 0)
               {
                  insertionFilm(id,titre,annee,pays,langue,
                             duree,resume,genres,realisateurNom,
                             realisateurId, scenaristes,
                             roles,poster,annonces);
                                    
                  id = -1;
                  annee = -1;
                  duree = -1;
                  titre = null;                                 
                  langue = null;                  
                  poster = null;
                  resume = null;
                  realisateurNom = null;
                  roleNom = null;
                  rolePersonnage = null;
                  realisateurId = -1;
                  roleId = -1;
                  
                  genres.clear();
                  scenaristes.clear();
                  roles.clear();
                  annonces.clear();  
                  pays.clear();
               }
               if (parser.getName().equals("role") && roleId >= 0) 
               {              
                  roles.add(new Role(roleId, roleNom, rolePersonnage));
                  roleId = -1;
                  roleNom = null;
                  rolePersonnage = null;
               }
            }
            else if (eventType == XmlPullParser.TEXT && id >= 0) 
            {
               if (tag != null)
               {                                    
                  if (tag.equals("titre"))
                     titre = parser.getText();
                  else if (tag.equals("annee"))
                     annee = Integer.parseInt(parser.getText());
                  else if (tag.equals("pays"))
                     pays.add(parser.getText());
                  else if (tag.equals("langue"))
                     langue = parser.getText();
                  else if (tag.equals("duree"))                 
                     duree = Integer.parseInt(parser.getText());
                  else if (tag.equals("resume"))                 
                     resume = parser.getText();
                  else if (tag.equals("genre"))
                     genres.add(parser.getText());
                  else if (tag.equals("realisateur"))
                     realisateurNom = parser.getText();
                  else if (tag.equals("scenariste"))
                     scenaristes.add(parser.getText());
                  else if (tag.equals("acteur"))
                     roleNom = parser.getText();
                  else if (tag.equals("personnage"))
                     rolePersonnage = parser.getText();
                  else if (tag.equals("poster"))
                     poster = parser.getText();
                  else if (tag.equals("annonce"))
                     annonces.add(parser.getText());                  
               }              
            }
            
            eventType = parser.next();            
         }
      }
      catch (XmlPullParserException e) {
          System.out.println(e);   
      }
      catch (IOException e) {
         System.out.println("IOException while parsing " + nomFichier); 
      }
   }
   
   public void lectureClients(String nomFichier){
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

         InputStream is = new FileInputStream(nomFichier);
         parser.setInput(is, null);

         int eventType = parser.getEventType();               

         String tag = null, 
                nomFamille = null,
                prenom = null,
                courriel = null,
                tel = null,
                anniv = null,
                adresse = null,
                ville = null,
                province = null,
                codePostal = null,
                carte = null,
                noCarte = null,
                motDePasse = null,
                forfait = null;                                 
         
         int id = -1,
             expMois = -1,
             expAnnee = -1;
         
         while (eventType != XmlPullParser.END_DOCUMENT) 
         {
            if(eventType == XmlPullParser.START_TAG) 
            {
               tag = parser.getName();
               
               if (tag.equals("client") && parser.getAttributeCount() == 1)
                  id = Integer.parseInt(parser.getAttributeValue(0));
            } 
            else if (eventType == XmlPullParser.END_TAG) 
            {                              
               tag = null;
               
               if (parser.getName().equals("client") && id >= 0)
               {

                  insertionClient(id,nomFamille,prenom,courriel,tel,
                             anniv,adresse,ville,province,
                             codePostal,carte,noCarte, 
                             expMois,expAnnee,motDePasse,forfait);               
                                    
                  nomFamille = null;
                  prenom = null;
                  courriel = null;               
                  tel = null;
                  anniv = null;
                  adresse = null;
                  ville = null;
                  province = null;
                  codePostal = null;
                  carte = null;
                  noCarte = null;
                  motDePasse = null; 
                  forfait = null;
                  
                  id = -1;
                  expMois = -1;
                  expAnnee = -1;
               }
            }
            else if (eventType == XmlPullParser.TEXT && id >= 0) 
            {         
               if (tag != null)
               {                                    
                  if (tag.equals("nom-famille"))
                     nomFamille = parser.getText();
                  else if (tag.equals("prenom"))
                     prenom = parser.getText();
                  else if (tag.equals("courriel"))
                     courriel = parser.getText();
                  else if (tag.equals("tel"))
                     tel = parser.getText();
                  else if (tag.equals("anniversaire"))
                     anniv = parser.getText();
                  else if (tag.equals("adresse"))
                     adresse = parser.getText();
                  else if (tag.equals("ville"))
                     ville = parser.getText();
                  else if (tag.equals("province"))
                     province = parser.getText();
                  else if (tag.equals("code-postal"))
                     codePostal = parser.getText();
                  else if (tag.equals("carte"))
                     carte = parser.getText();
                  else if (tag.equals("no"))
                     noCarte = parser.getText();
                  else if (tag.equals("exp-mois"))                 
                     expMois = Integer.parseInt(parser.getText());
                  else if (tag.equals("exp-annee"))                 
                     expAnnee = Integer.parseInt(parser.getText());
                  else if (tag.equals("mot-de-passe"))                 
                     motDePasse = parser.getText();  
                  else if (tag.equals("forfait"))                 
                     forfait = parser.getText(); 
               }              
            }
            
            eventType = parser.next();            
         }
      }
      catch (XmlPullParserException e) {
          System.out.println(e);   
      }
      catch (IOException e) {
         System.out.println("IOException while parsing " + nomFichier); 
      }
   }   
   
   private void insertionPersonne(int id, String nom, String prenom, String anniv, String lieu, String photo, String bio) {
    String sql = "INSERT INTO PROFESSIONNELS (NOM, PRENOM, DATE_NAISSANCE, LIEU_NAISSANCE, BIOGRAPHIE) VALUES (?, ?, ?, ?, ?)";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, nom);
        pstmt.setString(2, prenom);
        pstmt.setString(3, anniv);
        pstmt.setString(4, lieu);
        pstmt.setString(5, bio);

        pstmt.executeUpdate();
        System.out.println("Personne insérée avec succès : " + prenom + " " + nom);
    } catch (SQLException e) {
        System.err.println("Erreur lors de l'insertion de la personne dans la base de données");
        e.printStackTrace();
    }
}




    private void insertionFilm(int id, String titre, int annee, ArrayList<String> pays, String langue, int duree,
                           String resume, ArrayList<String> genres, String realisateurNom, int realisateurId,
                           ArrayList<String> scenaristes, ArrayList<Role> roles, String poster,
                           ArrayList<String> annonces) {

        String sqlFilm = "INSERT INTO FILMS (TITRE, ANNEE, RESUME, DUREE_MINUTE, ID_LANGUE) VALUES (?, ?, ?, ?, ?)";
        String sqlPays = "INSERT INTO FILMS_PAYS (ID_FILM, ID_PAYS) VALUES (?, ?)";
        String sqlGenre = "INSERT INTO FILMS_GENRES (ID_FILM, ID_GENRE) VALUES (?, ?)";
        String sqlRole = "INSERT INTO FILMS_ROLES (ID_FILM, ID_PROFESSIONNEL, PERSONNAGE) VALUES (?, ?, ?)";

        try {
            // Récupérer l'ID de la langue pour insertion
            int langueId = getLangueId(langue);
            if (langueId == -1) {
                System.err.println("Erreur : Langue inconnue " + langue);
                return;
            }

            // Insertion du film
            try (PreparedStatement pstmt = connection.prepareStatement(sqlFilm, new String[]{"ID_FILM"})) {
                pstmt.setString(1, titre);
                pstmt.setInt(2, annee);
                pstmt.setString(3, resume);
                pstmt.setInt(4, duree);
                pstmt.setInt(5, langueId);

                pstmt.executeUpdate();

                // Récupérer l'ID généré du film
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                    System.out.println("Film inséré avec succès, ID généré : " + id);
                } else {
                    throw new SQLException("L'insertion du film a échoué, aucun ID généré.");
                }
            }

            // Insertion des pays
            for (String paysNom : pays) {
                int paysId = getPaysId(paysNom);
                if (paysId > 0) {
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlPays)) {
                        pstmt.setInt(1, id);
                        pstmt.setInt(2, paysId);
                        pstmt.executeUpdate();
                    }
                }
            }

            // Insertion des genres
            for (String genreNom : genres) {
                int genreId = getGenreId(genreNom);
                if (genreId > 0) {
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlGenre)) {
                        pstmt.setInt(1, id);
                        pstmt.setInt(2, genreId);
                        pstmt.executeUpdate();
                    }
                }
            }

            // Insertion des rôles
            for (Role role : roles) {
                int professionnelId = getProfessionnelId(role.nom);
                if (professionnelId == -1) {
                    System.err.println("Erreur : Professionnel inconnu " + role.nom);
                    continue;
                }

                try (PreparedStatement pstmt = connection.prepareStatement(sqlRole)) {
                    pstmt.setInt(1, id);
                    pstmt.setInt(2, professionnelId);
                    pstmt.setString(3, role.personnage);
                    pstmt.executeUpdate();
                }
            }

            System.out.println("Tous les détails du film ont été insérés avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du film dans la base de données");
            e.printStackTrace();
        }
    }



   
    private void insertionClient(int id, String nomFamille, String prenom, String courriel, String tel, String anniv,
                             String rue, String ville, String province, String codePostal, String carte, 
                             String noCarte, int expMois, int expAnnee, String motDePasse, String forfait) {

    String sqlCheckPerson = "SELECT COUNT(*) FROM PERSONNES WHERE ID_PERSONNE = ?";
    String sqlPersonne = "INSERT INTO PERSONNES (ID_PERSONNE, PRENOM, NOM, EMAIL, TELEPHONE, ADRESSE_NUM_CIVIQUE, " +
                         "ADRESSE_RUE, ADRESSE_VILLE, ADRESSE_PROVINCE, ADRESSE_CODE_POSTAL, DATE_NAISSANCE, MOT_DE_PASSE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String sqlClient = "INSERT INTO CLIENTS (ID_CLIENT, ID_PERSONNE, ID_FORFAIT, TYPE_CARTE_CREDIT, NUMERO_CARTE_CREDIT, " +
                       "MOIS_EXPIRATION_CARTE, ANNEE_EXPIRATION_CARTE, CVV_CARTE_CREDIT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


    

    try {
        // Vérifier si la personne existe déjà
        boolean personneExiste = false;
        try (PreparedStatement pstmtCheckPerson = connection.prepareStatement(sqlCheckPerson)) {
            pstmtCheckPerson.setInt(1, id);
            ResultSet rs = pstmtCheckPerson.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                personneExiste = true;
            }
        }

        // Insérer la personne uniquement si elle n'existe pas déjà
        if (!personneExiste) {
            try (PreparedStatement pstmtPersonne = connection.prepareStatement(sqlPersonne)) {
                pstmtPersonne.setInt(1, id);
                pstmtPersonne.setString(2, prenom);
                pstmtPersonne.setString(3, nomFamille);
                pstmtPersonne.setString(4, courriel);
                pstmtPersonne.setString(5, tel);
                pstmtPersonne.setString(6, null);
                pstmtPersonne.setString(7, rue);
                pstmtPersonne.setString(8, ville);
                pstmtPersonne.setString(9, province);
                pstmtPersonne.setString(10, codePostal);
                pstmtPersonne.setString(11, anniv);
                pstmtPersonne.setString(12, motDePasse);
                pstmtPersonne.executeUpdate();
                System.out.println("Personne insérée avec succès : " + prenom + " " + nomFamille);
            }
        } else {
            System.out.println("Personne déjà existante : " + prenom + " " + nomFamille);
        }

        // Insertion du client
        try (PreparedStatement pstmtClient = connection.prepareStatement(sqlClient)) {
            pstmtClient.setInt(1, id);
            pstmtClient.setInt(2, id);
            pstmtClient.setString(3, forfait);
            pstmtClient.setString(4, carte);
            pstmtClient.setString(5, noCarte);
            pstmtClient.setInt(6, expMois);
            pstmtClient.setInt(7, expAnnee);
            pstmtClient.setInt(8, (int) ((Math.random() * 900) + 100));

            pstmtClient.executeUpdate();
            System.out.println("Client inséré avec succès : " + prenom + " " + nomFamille);
        }
    } catch (SQLException e) {
        System.err.println("Erreur lors de l'insertion du client dans la base de données");
        e.printStackTrace();
    }
}




    private int getPaysId(String nomPays) {
        String sqlSelect = "SELECT ID_PAYS FROM PAYS WHERE NOM = ?";
        String sqlInsert = "INSERT INTO PAYS (NOM) VALUES (?)";

        try (PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect)) {
            // Rechercher l'ID du pays
            pstmtSelect.setString(1, nomPays);
            ResultSet rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                // Le pays existe, retourner l'ID
                return rs.getInt("ID_PAYS");
            } else {
                // Le pays n'existe pas, on l'insère
                try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert, new String[]{"ID_PAYS"})) {
                    pstmtInsert.setString(1, nomPays);
                    pstmtInsert.executeUpdate();
                    
                    // Récupérer l'ID généré
                    ResultSet generatedKeys = pstmtInsert.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche ou de l'insertion du pays : " + nomPays);
            e.printStackTrace();
        }

        return -1;
    }


    private int getGenreId(String nomGenre) {
        String sqlSelect = "SELECT ID_GENRE FROM GENRES WHERE NOM = ?";
        String sqlInsert = "INSERT INTO GENRES (NOM) VALUES (?)";
    
        try (PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect)) {
            // Rechercher l'ID du genre
            pstmtSelect.setString(1, nomGenre);
            ResultSet rs = pstmtSelect.executeQuery();
    
            if (rs.next()) {
                // Le genre existe, retourner l'ID
                return rs.getInt("ID_GENRE");
            } else {
                // Le genre n'existe pas, on l'insère
                try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert, new String[]{"ID_GENRE"})) {
                    pstmtInsert.setString(1, nomGenre);
                    pstmtInsert.executeUpdate();
                    
                    // Récupérer l'ID généré
                    ResultSet generatedKeys = pstmtInsert.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche ou de l'insertion du genre : " + nomGenre);
            e.printStackTrace();
        }
    
        return -1; 
    }


    private int getLangueId(String nomLangue) {
        String sqlSelect = "SELECT ID_LANGUE FROM LANGUES WHERE NOM = ?";
        String sqlInsert = "INSERT INTO LANGUES (NOM) VALUES (?)";
    
        try (PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect)) {
            // Rechercher l'ID de la langue
            pstmtSelect.setString(1, nomLangue);
            ResultSet rs = pstmtSelect.executeQuery();
    
            if (rs.next()) {
                // La langue existe, retourner l'ID
                return rs.getInt("ID_LANGUE");
            } else {
                // La langue n'existe pas, on l'insère
                try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert, new String[]{"ID_LANGUE"})) {
                    pstmtInsert.setString(1, nomLangue);
                    pstmtInsert.executeUpdate();
                    
                    // Récupérer l'ID généré
                    ResultSet generatedKeys = pstmtInsert.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche ou de l'insertion de la langue : " + nomLangue);
            e.printStackTrace();
        }
    
        return -1; 
    }
    


    private int getProfessionnelId(String nomComplet) {
        String sqlSelect = "SELECT ID_PROFESSIONNEL FROM PROFESSIONNELS WHERE NOM = ? AND PRENOM = ?";
        String sqlInsert = "INSERT INTO PROFESSIONNELS (NOM, PRENOM) VALUES (?, ?)";
    
        // Supposons que le nom complet est sous la forme "Prénom Nom"
        String[] nomPrenom = nomComplet.split(" ", 2);
        String prenom = nomPrenom.length > 1 ? nomPrenom[0] : "";
        String nom = nomPrenom.length > 1 ? nomPrenom[1] : nomPrenom[0];
    
        try (PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect)) {
            // Rechercher l'ID du professionnel
            pstmtSelect.setString(1, nom);
            pstmtSelect.setString(2, prenom);
            ResultSet rs = pstmtSelect.executeQuery();
    
            if (rs.next()) {
                // Le professionnel existe, retourner l'ID
                return rs.getInt("ID_PROFESSIONNEL");
            } else {
                // Le professionnel n'existe pas, on l'insère
                try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert, new String[]{"ID_PROFESSIONNEL"})) {
                    pstmtInsert.setString(1, nom);
                    pstmtInsert.setString(2, prenom);
                    pstmtInsert.executeUpdate();
    
                    // Récupérer l'ID généré
                    ResultSet generatedKeys = pstmtInsert.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche ou de l'insertion du professionnel : " + nomComplet);
            e.printStackTrace();
        }
    
        return -1; 
    }

   
   private void connectionBD() {
      // On se connecte a la BD
      String url = "jdbc:oracle:thin:@//bdlog660.ens.ad.etsmtl.ca:1521/ORCLPDB.ens.ad.etsmtl.ca";
      String user = "EQUIPE210";
      String password = "XFC5ioxE";

      try {
         // Charger le driver Oracle JDBC
         Class.forName("oracle.jdbc.driver.OracleDriver");
         
         // Établir la connexion
         connection = DriverManager.getConnection(url, user, password);
         System.out.println("Connexion à la base de données établie avec succès !");
      } catch (ClassNotFoundException e) {
         System.err.println("Driver JDBC Oracle non trouvé !");
         e.printStackTrace();
      } catch (java.sql.SQLException e) {
         System.err.println("Erreur lors de la connexion à la base de données !");
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {
      LectureBD lecture = new LectureBD();
      
      //lecture.lecturePersonnes(args[2]);
      lecture.lectureClients(args[0]);
      lecture.lectureFilms(args[1]);
      
   }
}
