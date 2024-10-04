import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class LectureBD {

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

    public void lecturePersonnes(String nomFichier) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            InputStream is = new FileInputStream(nomFichier);
            parser.setInput(is, null);

            int eventType = parser.getEventType();

            String tag = null, nom = null, anniversaire = null, lieu =
                null, photo = null, bio = null;

            int id = -1;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tag = parser.getName();

                    if (
                        tag.equals("personne") &&
                        parser.getAttributeCount() == 1
                    ) id = Integer.parseInt(parser.getAttributeValue(0));
                } else if (eventType == XmlPullParser.END_TAG) {
                    tag = null;

                    if (parser.getName().equals("personne") && id >= 0) {
                        insertionPersonne(
                            id,
                            nom,
                            anniversaire,
                            lieu,
                            photo,
                            bio
                        );

                        id = -1;
                        nom = null;
                        anniversaire = null;
                        lieu = null;
                        photo = null;
                        bio = null;
                    }
                } else if (eventType == XmlPullParser.TEXT && id >= 0) {
                    if (tag != null) {
                        if (tag.equals("nom")) nom = parser.getText();
                        else if (tag.equals("anniversaire")) anniversaire =
                            parser.getText();
                        else if (tag.equals("lieu")) lieu = parser.getText();
                        else if (tag.equals("photo")) photo = parser.getText();
                        else if (tag.equals("bio")) bio = parser.getText();
                    }
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println("IOException while parsing " + nomFichier);
        }
    }

    public void lectureFilms(String nomFichier) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            InputStream is = new FileInputStream(nomFichier);
            parser.setInput(is, null);

            int eventType = parser.getEventType();

            String tag = null, titre = null, langue = null, poster =
                null, roleNom = null, rolePersonnage = null, realisateurNom =
                null, resume = null;

            ArrayList<String> pays = new ArrayList<String>();
            ArrayList<String> genres = new ArrayList<String>();
            ArrayList<String> scenaristes = new ArrayList<String>();
            ArrayList<Role> roles = new ArrayList<Role>();
            ArrayList<String> annonces = new ArrayList<String>();

            int id = -1, annee = -1, duree = -1, roleId = -1, realisateurId =
                -1;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tag = parser.getName();

                    if (
                        tag.equals("film") && parser.getAttributeCount() == 1
                    ) id = Integer.parseInt(parser.getAttributeValue(0));
                    else if (
                        tag.equals("realisateur") &&
                        parser.getAttributeCount() == 1
                    ) realisateurId = Integer.parseInt(
                        parser.getAttributeValue(0)
                    );
                    else if (
                        tag.equals("acteur") && parser.getAttributeCount() == 1
                    ) roleId = Integer.parseInt(parser.getAttributeValue(0));
                } else if (eventType == XmlPullParser.END_TAG) {
                    tag = null;

                    if (parser.getName().equals("film") && id >= 0) {
                        insertionFilm(
                            id,
                            titre,
                            annee,
                            pays,
                            langue,
                            duree,
                            resume,
                            genres,
                            realisateurNom,
                            realisateurId,
                            scenaristes,
                            roles,
                            poster,
                            annonces
                        );

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
                    if (parser.getName().equals("role") && roleId >= 0) {
                        roles.add(new Role(roleId, roleNom, rolePersonnage));
                        roleId = -1;
                        roleNom = null;
                        rolePersonnage = null;
                    }
                } else if (eventType == XmlPullParser.TEXT && id >= 0) {
                    if (tag != null) {
                        if (tag.equals("titre")) titre = parser.getText();
                        else if (tag.equals("annee")) annee = Integer.parseInt(
                            parser.getText()
                        );
                        else if (tag.equals("pays")) pays.add(parser.getText());
                        else if (tag.equals("langue")) langue =
                            parser.getText();
                        else if (tag.equals("duree")) duree = Integer.parseInt(
                            parser.getText()
                        );
                        else if (tag.equals("resume")) resume =
                            parser.getText();
                        else if (tag.equals("genre")) genres.add(
                            parser.getText()
                        );
                        else if (tag.equals("realisateur")) realisateurNom =
                            parser.getText();
                        else if (tag.equals("scenariste")) scenaristes.add(
                            parser.getText()
                        );
                        else if (tag.equals("acteur")) roleNom =
                            parser.getText();
                        else if (tag.equals("personnage")) rolePersonnage =
                            parser.getText();
                        else if (tag.equals("poster")) poster =
                            parser.getText();
                        else if (tag.equals("annonce")) annonces.add(
                            parser.getText()
                        );
                    }
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println("IOException while parsing " + nomFichier);
        }
    }

    public void lectureClients(String nomFichier) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            InputStream is = new FileInputStream(nomFichier);
            parser.setInput(is, null);

            int eventType = parser.getEventType();

            String tag = null, nomFamille = null, prenom = null, courriel =
                null, tel = null, anniv = null, adresse = null, ville =
                null, province = null, codePostal = null, carte =
                null, noCarte = null, motDePasse = null, forfait = null;

            int id = -1, expMois = -1, expAnnee = -1;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tag = parser.getName();

                    if (
                        tag.equals("client") && parser.getAttributeCount() == 1
                    ) id = Integer.parseInt(parser.getAttributeValue(0));
                } else if (eventType == XmlPullParser.END_TAG) {
                    tag = null;

                    if (parser.getName().equals("client") && id >= 0) {
                        insertionClient(
                            id,
                            nomFamille,
                            prenom,
                            courriel,
                            tel,
                            anniv,
                            adresse,
                            ville,
                            province,
                            codePostal,
                            carte,
                            noCarte,
                            expMois,
                            expAnnee,
                            motDePasse,
                            forfait
                        );

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
                } else if (eventType == XmlPullParser.TEXT && id >= 0) {
                    if (tag != null) {
                        if (tag.equals("nom-famille")) nomFamille =
                            parser.getText();
                        else if (tag.equals("prenom")) prenom =
                            parser.getText();
                        else if (tag.equals("courriel")) courriel =
                            parser.getText();
                        else if (tag.equals("tel")) tel = parser.getText();
                        else if (tag.equals("anniversaire")) anniv =
                            parser.getText();
                        else if (tag.equals("adresse")) adresse =
                            parser.getText();
                        else if (tag.equals("ville")) ville = parser.getText();
                        else if (tag.equals("province")) province =
                            parser.getText();
                        else if (tag.equals("code-postal")) codePostal =
                            parser.getText();
                        else if (tag.equals("carte")) carte = parser.getText();
                        else if (tag.equals("no")) noCarte = parser.getText();
                        else if (tag.equals("exp-mois")) expMois =
                            Integer.parseInt(parser.getText());
                        else if (tag.equals("exp-annee")) expAnnee =
                            Integer.parseInt(parser.getText());
                        else if (tag.equals("mot-de-passe")) motDePasse =
                            parser.getText();
                        else if (tag.equals("forfait")) forfait =
                            parser.getText();
                    }
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println("IOException while parsing " + nomFichier);
        }
    }

    private void insertionPersonne(
        int id,
        String nom,
        String anniv,
        String lieu,
        String photo,
        String bio
    ) {
        String sql =
            "INSERT INTO Personne (id, nom, date_naissance, lieu_naissance, photo, biographie) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, nom);
            pstmt.setDate(3, anniv != null ? Date.valueOf(anniv) : null);
            pstmt.setString(4, lieu);
            pstmt.setString(5, photo);
            pstmt.setString(6, bio);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(
                "Erreur lors de l'insertion de la personne: " + e.getMessage()
            );
        }
    }

    public void insererFilm(
        int id,
        String titre,
        int annee,
        ArrayList<String> pays,
        String langue,
        int duree,
        String resume,
        ArrayList<String> genres,
        String realisateurNom,
        int realisateurId,
        ArrayList<String> scenaristes,
        ArrayList<LectureBD.Role> roles,
        String poster,
        ArrayList<String> annonces
    ) {
        String sqlFilm =
            "INSERT INTO Film (id, titre, annee, langue, duree, resume, poster, realisateur_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmtFilm = conn.prepareStatement(sqlFilm)) {
            conn.setAutoCommit(false);

            pstmtFilm.setInt(1, id);
            pstmtFilm.setString(2, titre);
            pstmtFilm.setInt(3, annee);
            pstmtFilm.setString(4, langue);
            pstmtFilm.setInt(5, duree);
            pstmtFilm.setString(6, resume);
            pstmtFilm.setString(7, poster);
            pstmtFilm.setInt(8, realisateurId);
            pstmtFilm.executeUpdate();

            // Insertion des pays
            insererPaysFilm(id, pays);

            // Insertion des genres
            insererGenresFilm(id, genres);

            // Insertion des scénaristes
            insererScenaristesFilm(id, scenaristes);

            // Insertion des rôles
            insererRolesFilm(id, roles);

            // Insertion des annonces
            insererAnnoncesFilm(id, annonces);

            // Génération aléatoire du nombre de copies entre 1 et 100
            int nombreCopies = random.nextInt(100) + 1;
            String sqlCopies =
                "INSERT INTO Copies (film_id, nombre_disponible) VALUES (?, ?)";
            try (
                PreparedStatement pstmtCopies = conn.prepareStatement(sqlCopies)
            ) {
                pstmtCopies.setInt(1, id);
                pstmtCopies.setInt(2, nombreCopies);
                pstmtCopies.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println(
                    "Erreur lors du rollback: " + ex.getMessage()
                );
            }
            System.out.println(
                "Erreur lors de l'insertion du film: " + e.getMessage()
            );
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(
                    "Erreur lors de la réactivation de l'autocommit: " +
                    e.getMessage()
                );
            }
        }
    }

    private void insererPaysFilm(int filmId, ArrayList<String> pays)
        throws SQLException {
        String sql = "INSERT INTO Film_Pays (film_id, pays) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String p : pays) {
                pstmt.setInt(1, filmId);
                pstmt.setString(2, p);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void insererGenresFilm(int filmId, ArrayList<String> genres)
        throws SQLException {
        String sql = "INSERT INTO Film_Genre (film_id, genre) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String genre : genres) {
                pstmt.setInt(1, filmId);
                pstmt.setString(2, genre);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void insererScenaristesFilm(
        int filmId,
        ArrayList<String> scenaristes
    ) throws SQLException {
        String sql =
            "INSERT INTO Film_Scenariste (film_id, scenariste) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String scenariste : scenaristes) {
                pstmt.setInt(1, filmId);
                pstmt.setString(2, scenariste);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void insererRolesFilm(int filmId, ArrayList<LectureBD.Role> roles)
        throws SQLException {
        String sql =
            "INSERT INTO Role (film_id, personne_id, nom_personnage) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (LectureBD.Role role : roles) {
                pstmt.setInt(1, filmId);
                pstmt.setInt(2, role.id);
                pstmt.setString(3, role.personnage);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void insererAnnoncesFilm(int filmId, ArrayList<String> annonces)
        throws SQLException {
        String sql = "INSERT INTO Annonce (film_id, url) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String annonce : annonces) {
                pstmt.setInt(1, filmId);
                pstmt.setString(2, annonce);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public void insererClient(
        int id,
        String nomFamille,
        String prenom,
        String courriel,
        String tel,
        String anniv,
        String adresse,
        String ville,
        String province,
        String codePostal,
        String carte,
        String noCarte,
        int expMois,
        int expAnnee,
        String motDePasse,
        String forfait
    ) {
        String sql =
            "INSERT INTO Client (id, nom_famille, prenom, courriel, telephone, date_naissance, adresse, ville, province, code_postal, type_carte, numero_carte, exp_mois, exp_annee, mot_de_passe, forfait) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, nomFamille);
            pstmt.setString(3, prenom);
            pstmt.setString(4, courriel);
            pstmt.setString(5, tel);
            pstmt.setDate(6, anniv != null ? Date.valueOf(anniv) : null);
            pstmt.setString(7, adresse);
            pstmt.setString(8, ville);
            pstmt.setString(9, province);
            pstmt.setString(10, codePostal);
            pstmt.setString(11, carte);
            pstmt.setString(12, noCarte);
            pstmt.setInt(13, expMois);
            pstmt.setInt(14, expAnnee);
            pstmt.setString(15, motDePasse);
            pstmt.setString(16, forfait);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(
                "Erreur lors de l'insertion du client: " + e.getMessage()
            );
        }
    }

    private void connectionBD() {
        // On se connecte a la BD
        try {
            // Assurez-vous de remplacer ces valeurs par celles de votre base de données
            String url = "jdbc:mysql://localhost:3306/votre_base_de_donnees";
            String user = "votre_utilisateur";
            String password = "votre_mot_de_passe";

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion à la base de données réussie.");
        } catch (SQLException e) {
            System.out.println(
                "Erreur de connexion à la base de données: " + e.getMessage()
            );
        }
    }

    public static void main(String[] args) {
        LectureBD lecture = new LectureBD();

        lecture.lecturePersonnes(args[0]);
        lecture.lectureFilms(args[1]);
        lecture.lectureClients(args[2]);
    }
}
