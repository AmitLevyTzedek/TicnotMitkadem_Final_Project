package configs;

import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import graph.Agent;
import graph.ParallelAgent;


public class GenericConfig implements Config {
    private String confFile;
    private List<ParallelAgent> agents = new ArrayList<>();

    // Set קובץ קונפיגורציה
    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }

//    @Override
//    public void create() {
//        if (confFile == null) {
//            System.out.println("Error: Configuration file is not set.");
//            return;
//        }
//
//        try (BufferedReader br1 = new BufferedReader(new FileReader(confFile))) {
//            String line;
//            boolean isValid;
//            //while ((line = br1.readLine()) != null) {System.out.println("kine:"+line);}
//
//            BufferedReader br = new BufferedReader(new FileReader(confFile));
//            while ((line = br.readLine()) != null) {
//                isValid = true;
//
//                // קריאת שם הפרויקט (שורה 1 של כל קבוצה)
//                String line_name = line.trim();
//                //System.out.println("line_name: " + line_name);
//
////                if ( !line_name.startsWith("test")) {
////                    //System.out.println("Error: The project name in the format 'tichnotmitkadm1.test' is missing or incorrect.");
////                    isValid = false;
////                }
//
//                // קריאת שורת ה-Publishers (שורה 2 של כל קבוצה)
//                String line_subs = br.readLine().trim();
//                //System.out.println("line_pubs: " + line_pubs);
//                if (line_subs.isEmpty()) {
//                    //System.out.println("Error: The second line must contain topic names separated by commas.");
//                    isValid = false;
//                }
//
//                // קריאת שורת ה-Subscribers (שורה 3 של כל קבוצה)
//                String line_pubs = br.readLine().trim();
//                //System.out.println("line_subs: " + line_subs);
//                if (line_pubs.isEmpty()) {
//                    System.out.println("Error: The third line must contain topic names separated by commas.");
//                    isValid = false;
//                }
//
//                // אם כל השורות תקינות, נמשיך
//                if (isValid) {
//                    // נוודא שהנושאים קיימים ב-TopicManager
//                    TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
//
//                    // פיצול השורות לנושאים של subscribers ו-publishers
//                    List<String> subs = Arrays.asList(line_subs.split(","));
//                    //System.out.println("subs: " + subs);
//
//                    List<String> pubs = Arrays.asList(line_pubs.split(","));
//                   // System.out.println("pubs: " + pubs);
//
//                    // בדיקת תקינות הנושאים (אם קיימים ב-TopicManager)
//                    for (String topicName : subs) {
//                        if (topicManager.getTopic(topicName) == null) {
//                            System.out.println("Error: Topic " + topicName + " does not exist in the TopicManager.");
//                            isValid = false;
//                        }
//                    }
//
//                    for (String topicName : pubs) {
//                        if (topicManager.getTopic(topicName) == null) {
//                            System.out.println("Error: Topic " + topicName + " does not exist in the TopicManager.");
//                            isValid = false;
//                        }
//                    }
//
//                    // אם כל הנושאים קיימים, ניצור את הסוכן
//                    if (isValid) {
//                        // יצירת סוכן בעזרת Reflection
//                        //Class<?> agentClass = Class.forName("configs.PlusAgent");  // קבלת מחלקת הסוכן
//                        String[] class_name=line_name.split("\\.");
//                        String Cname=class_name[class_name.length-2]+"."+class_name[class_name.length-1];
//                        //Class<?> agentClass = Class.forName(line_name);  // קבלת מחלקת הסוכן
//                        Class<?> agentClass = Class.forName(Cname);  // קבלת מחלקת הסוכן
//                        Constructor<?> constructor = agentClass.getConstructor(List.class, List.class);  // בנאי עם פרמטרים
//                        //System.out.println("create agent with subs: " + line_subs+", pubs: " + line_pubs);
//                        Agent agent = (Agent) constructor.newInstance(subs, pubs);  // יצירת מופע של הסוכן
//
//                        // עטיפת הסוכן ב-ParallelAgent
//                        int queueCapacity = 10;  // קיבולת התור
//                        ParallelAgent parallelAgent = new ParallelAgent(agent, queueCapacity);
//                        agents.add(parallelAgent);  // הוספת הסוכן המקבילי לרשימה
//                        //System.out.println("added agent " );
//                    }
//                }
//            }
//        } catch (IOException | ReflectiveOperationException e) {
//            System.out.println("Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
    @Override
    public void create() {
        if (confFile == null) {
            throw new IllegalStateException("Configuration file is null");
        }

        // try to read the data from the file
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(confFile), "UTF-8"))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }

            for (int i = 0; i < lines.size(); i += 3) {
                String fullClassName = lines.get(i);
                String[] classNameParts = fullClassName.split("\\.");
                String className = classNameParts[classNameParts.length - 2] + "." + classNameParts[classNameParts.length - 1];  // Extract last part after the last "."

                String[] subs = lines.get(i + 1).isEmpty() ? new String[0] : lines.get(i + 1).split(",");
                String[] pubs = lines.get(i + 2).isEmpty() ? new String[0] : lines.get(i + 2).split(",");


                // try to create an agent class from the class name and then convert to parallel agent
                try {
                    Class<?> agent_class = Class.forName(className);
                    //System.out.println("reached after class name");
                    Constructor<?> constructor = agent_class.getConstructor(String[].class, String[].class);

                    Object agentInstance = constructor.newInstance((Object) subs, (Object) pubs);

                    ParallelAgent parallelAgent = new ParallelAgent((Agent) agentInstance, 10);
                    agents.add(parallelAgent);

                } catch (ClassNotFoundException e) {
                    System.err.println("ERROR: Class not found -> [" + fullClassName + "]");
                    throw new RuntimeException("Class not found: [" + fullClassName + "]", e);
                } catch (NoSuchMethodException e) {
                    System.err.println("ERROR: Constructor not found for " + className);
                    throw new RuntimeException("Invalid constructor for: " + className, e);
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to instantiate " + className);
                    throw new RuntimeException("Error instantiating: " + className, e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading configuration", e);
        }
    }

    @Override
    public String getName() {
        return "Generic Config";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void close() {
        for (ParallelAgent agent : agents) {
            agent.close();  // סוגר כל סוכן
        }
        agents.clear();
    }
}
