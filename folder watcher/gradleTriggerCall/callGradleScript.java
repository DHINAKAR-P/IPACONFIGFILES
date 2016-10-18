package gradleTriggerCall;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.sql.Types;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;


public class callGradleScript {

	private static final String driverClassName = "com.mysql.jdbc.Driver";	
	    private static final String url = "jdbc:mysql://localhost/companydb";	
	    private static final String dbUsername = "gepuser";	
	    private static final String dbPassword = "tang3456";
	    private static final String insertValues="INSERT INTO companydb.job (" + " status," + "directory_name) " +  "VALUES (?, ?);";
	    
	  //  private static DriverManagerDataSource dataSource;
	
    public static String watchDirectoryPath(Path path) {
    	String commandline="";
        // Check if path is a folder
        try {
            Boolean isFolder = (Boolean) Files.getAttribute(path,
                    "basic:isDirectory", NOFOLLOW_LINKS);
            if (!isFolder) {
                throw new IllegalArgumentException("Path: " + path
                        + " is not a folder");
            }
        } catch (IOException ioe) {
            // Folder does not exists
            ioe.printStackTrace();
        }

        System.out.println("Watching path: " + path);

        // We obtain the file system of the Path
        FileSystem fs = path.getFileSystem();
        
        System.out.println("------"+fs.toString());

        // We create the new WatchService using the new try() block
        try (WatchService service = fs.newWatchService()) {
            // We register the path to the service
            // We watch for creation events    
        	 path.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE); 

            // Start the infinite polling loop
            WatchKey key = null;
            while (true) {
                key = service.take();
                // Dequeuing events
                Kind<?> kind = null;
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    // Get the type of the event
                    kind = watchEvent.kind();
                    System.out.println("kind---------"+kind);
                    if (OVERFLOW == kind) {
                        continue;
                    } else if (ENTRY_CREATE == kind) {
                    	System.out.println("dir created");
                        @SuppressWarnings("unchecked")
						Path newPath = ((WatchEvent<Path>) watchEvent).context();
                        //saveJob(newPath.toString(),"directory_modified");
                        System.out.println("New path created: " + newPath);
                    } else if (ENTRY_MODIFY == kind) {
                    	try{
                    	@SuppressWarnings("unchecked")
						Path modified = ((WatchEvent<Path>) watchEvent).context();
                        //saveJob(modified.toString(),"directory_modified");
                        System.out.println("Directory----- modified: " + modified);
                        //Process p = Runtime.getRuntime().exec("C:\\server deployment\\apache-tomcat-8.0.18\\bin\\startup.bat");
                        Process p = Runtime.getRuntime().exec("/Users/administrator/"+modified+"/executable.sh");
            			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            			String line;
            			while ((line = reader.readLine()) != null) {
            				System.out.println(line);
            				commandline+=("\n"+line);
            			}
            			p.waitFor();
            			p.destroy();
                    	}catch (IOException e1) {
                		} catch (InterruptedException e2) {
                		}
                		//return commandline;
                    }
                }

                if (!key.reset()) {
                    break;
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
 return commandline;
    }

   /* 
    public static DriverManagerDataSource getDataSource() {	    	 
    	  DriverManagerDataSource dataSource = new DriverManagerDataSource(); 
    	  dataSource.setDriverClassName(driverClassName);
    	  dataSource.setUrl(url);
    	  dataSource.setUsername(dbUsername);
    	  dataSource.setPassword(dbPassword);
    	  System.out.println("----UserName for database connection---"+dataSource.getUsername());
    	  return dataSource;
    	    }
    
    public static void saveJob(String status, String directory_name) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        Object[] params = new Object[] {status, directory_name};
        int[] types = new int[] {Types.VARCHAR, Types.VARCHAR};
        int row = template.update(insertValues, params, types);
        System.out.println(row + "One row inserted in Job---table.");
    }
    */
    public static void main(String[] args) throws IOException,
            InterruptedException {
    	//dataSource = getDataSource();
        // Paths.get(System.getProperty("C:\\Users\\Dhinkar\\Downloads"));
        //File dir = new File("C:\\Users\\Dhinkar\\Downloads\\Documents");
    	File dir = new File("/Users/administrator/Documents");
        watchDirectoryPath(dir.toPath());
    }
    }