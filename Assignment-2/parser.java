import java.sql.*;
import java.io.*;
import java.util.*;

public class Group_15 {
    static final String DB_URL = "jdbc:postgresql://localhost:5432/Group_15_Assignment_2";
    static final String USER = "postgres";
    static final String PASS = "password";

    static Set<String> allperms = new HashSet<String>();

    public static void permute(java.util.List<String> arr, int k) {
        for (int i = k; i < arr.size(); i++) {
            java.util.Collections.swap(arr, i, k);
            permute(arr, k + 1);
            java.util.Collections.swap(arr, k, i);
        }
        if (k == arr.size() - 1) {
            String s = "";
            for (int i = 0; i < arr.size(); i++) {
                s += arr.get(i);
                s += " ";
            }
            s.strip();
            allperms.add(s);
        }
    }



    public static void get_non_canonical(String name) {
        java.util.List<String> arrOfName = new ArrayList<>(Arrays.asList(name.split(" ", 5)));
        permute(arrOfName, 0);

    }

    public static String[] get_authors(String st) throws Exception {
        byte[] buf = new byte[1];
        String[] mult_auth = new String[150];
        String temp = new String(buf, "UTF-8");
        temp = "";
        int num_auth = 0;

        for (int p = 2; p < st.length(); p++) {
            if (st.charAt(p) == ',') {
                if(temp.equals("")) {
                    continue;
                }
                if(temp.charAt(0) == ' ') {
                    if(num_auth != 0) {
                        num_auth--;
                        mult_auth[num_auth] = mult_auth[num_auth] + temp;
                        mult_auth[num_auth] = mult_auth[num_auth].replace(".", "");
                        mult_auth[num_auth] = mult_auth[num_auth].trim();
                    }
                }
                if (temp.equals("Jr.")) {
                    
                     if(num_auth == 0){
                        mult_auth[num_auth] = temp;
                        temp = "";
                        p++;
                        for( ; p < st.length();)
                        {
                         if (st.charAt(p) == ',') {
                            break;
                         }
                         else {
                            temp += st.charAt(p);
                            p++;
                         }
                        }
                        mult_auth[num_auth]  = mult_auth[num_auth] + temp;
                        mult_auth[num_auth] = mult_auth[num_auth].replace(".", "");
                        mult_auth[num_auth] = mult_auth[num_auth].trim();
                    }
                } else {
                    mult_auth[num_auth] = temp;
                    mult_auth[num_auth] = mult_auth[num_auth].replace(".", "");
                    mult_auth[num_auth] = mult_auth[num_auth].trim();
                }
                temp = "";
                num_auth++;
            } else {
                temp += st.charAt(p);
            }
        }
        
        mult_auth[num_auth] = temp;
        mult_auth[num_auth] = mult_auth[num_auth].replace(".", "");
        mult_auth[num_auth] = mult_auth[num_auth].trim();
        return mult_auth;
    }

    public static void main(String[] args) throws Exception {
        File file = new File("source.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        byte[] buf = new byte[1];

        String st = new String(buf, "UTF-8");

       

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

                PreparedStatement pStmt_author = conn.prepareStatement("INSERT INTO author(canonical_name) VALUES(?)");
                PreparedStatement pStmt_non_canonical = conn
                        .prepareStatement("INSERT INTO non_canonical(canonical_name,non_canonical_name) VALUES(?,?)");
                PreparedStatement pStmt_publication_venue = conn.prepareStatement(
                        "INSERT INTO publication_venue(venue_name,venue_type) VALUES(?,?)");
                PreparedStatement pStmt_research_paper = conn.prepareStatement(
                        "INSERT INTO research_paper(paper_id,abstract,publication_year,title,venue_name,corresponding_author_canonical_name) VALUES(?,?,?,?,?,?)");
                PreparedStatement pStmt_citations = conn
                        .prepareStatement("INSERT INTO citations(main_paper_id,ref_paper_id) VALUES(?,?)");
                PreparedStatement pStmt_writtenby = conn
                        .prepareStatement("INSERT INTO writtenby(paper_id, author_name ,author_rank) VALUES(?,?,?)");

        ) {
            
            String[] multiauth = new String[10];
            
            int id = 0;
            boolean if_has_abst = false;
            while ((st = br.readLine()) != null) {
                
                if (st.length() < 1){
                    if(if_has_abst == false) {
                        pStmt_research_paper.setString(2, null);
                    }
                    pStmt_research_paper.executeUpdate();
                    if_has_abst = false;
                    continue;
                }

                if (st.charAt(1) == '*') {

                    String title = st.substring(2, st.length());
                    pStmt_research_paper.setString(4, title);

                } else if (st.charAt(1) == '@') {

                    multiauth = get_authors(st);                   

                    pStmt_research_paper.setString(6, multiauth[0]);
                    

                    for (int i = 0; i < multiauth.length; i++) {
                        if(multiauth[i]!=null)
                        {
                            pStmt_author.setString(1, multiauth[i]);
                            pStmt_author.executeUpdate();
                            if((multiauth[i].length() - multiauth[i].replace(" ", "").length()) > 4) {
                                pStmt_non_canonical.setString(1, multiauth[i]);
                                pStmt_non_canonical.setString(2, multiauth[i]);
                                pStmt_non_canonical.executeUpdate();
                            }
                            else {
                                get_non_canonical(multiauth[i]);
                                for (String s : allperms) {
                                    pStmt_non_canonical.setString(1, multiauth[i]);
                                    pStmt_non_canonical.setString(2, s);
                                    pStmt_non_canonical.executeUpdate();
                                }
                                allperms.clear();
                            }
                        }
                    }

                } else if (st.charAt(1) == 't') {
                    int year = Integer.parseInt(st.substring(2, st.length()));
                    pStmt_research_paper.setInt(3, year);

                } else if (st.charAt(1) == 'i') {
                    id = Integer.parseInt(st.substring(6, st.length()));
                    pStmt_research_paper.setInt(1, id);
                    int rank=0;

                    for (int i = 0; i < multiauth.length; i++) {


                        if(multiauth[i]!=null)
                        {
                            rank++;
                            pStmt_writtenby.setInt(1, id);
                            pStmt_writtenby.setString(2, multiauth[i]);
                            pStmt_writtenby.setInt(3,rank);
                            pStmt_writtenby.executeUpdate();
                        }

                    }

                } else if (st.charAt(1) == 'c') {

                    String venue_name = (st.substring(2, st.length()));
                    String venue_type;

                    if (st.toLowerCase().contains("proceedings") || st.toLowerCase().contains("conference")) {
                        venue_type = "conference";
                    } else if (st.substring(2, st.length()).equals("")) {
                        venue_type = "";
                    } else {
                        venue_type = "journal";
                    }
                    pStmt_research_paper.setString(5, venue_name);
                    if(!venue_name.equals("")) {
                       pStmt_publication_venue.setString(1, venue_name);
                        pStmt_publication_venue.setString(2, venue_type);
                        pStmt_publication_venue.executeUpdate();
                    }

                }
                else if (st.charAt(1) == '%') {
                    String ref_str = st.substring(2, st.length());
                    if(ref_str==null || ref_str.strip()=="" )
                    {   
                       
                        continue;
                    }

                    int ref_id = Integer.parseInt(ref_str);
                    pStmt_citations.setInt(1, id);
                    pStmt_citations.setInt(2, ref_id);
                    pStmt_citations.executeUpdate();
                }
                else if (st.charAt(1) == '!') {
                    if_has_abst = true;
                    String abstrct = st.substring(2, st.length());
                    pStmt_research_paper.setString(2, abstrct);
                }

            }
            br.close();
            if(if_has_abst == false) {
                pStmt_research_paper.setString(2, null);
            }
            pStmt_research_paper.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}