import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



public class Server {
    
	JTextArea area;    
    Server(){
        JFrame main_window = new JFrame("Server");
        main_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_window.setSize(200, 300); 
        area = new JTextArea();
		JScrollPane scroll = new JScrollPane(area);
		main_window.getContentPane().add(scroll);
		area.setEditable(false);
        main_window.setVisible(true);
        connect();
        
    }
   
    public void connect(){
        int port = 5555;
		String answer;
		
		area.setText(null);
        try {
            ServerSocket ss = new ServerSocket(port);
            area.append ("Local IP address:\n" + 
            InetAddress.getLocalHost().toString() + "\n\n");
			int r=0;
			
            while(true){
				area.append("Waiting for connection\n");
                Socket socket = ss.accept();
				Process prog;
				area.append("Connected\n");
                InputStream in = socket.getInputStream();
				byte[] str = new byte[64 * 1024];
				r = in.read(str);
				String data = new String(str, 0, r);
                area.append(data+"\n");
				
				if (data.equals("2"))
				{
					if (checkInet())
					{
						answer="InetON";
						area.append("Inet_ON\n");
					}
					else
					{
						answer="InetOFF";
						area.append("Inet_OFF\n");
					}
					socket.getOutputStream().write(answer.getBytes());
				}
				else 
				{
					if (data.equals("3"))
					{
						prog = Runtime.getRuntime().exec("kill.bat");
						answer = "Stream's stopped";
						socket.getOutputStream().write(answer.getBytes());
					}
					else
					{
						try {
							String prog_name = "stream"+data+".bat";
							prog = Runtime.getRuntime().exec(prog_name);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}	
						answer="Stream's started (" + data + ")";
						socket.getOutputStream().write(answer.getBytes());
					}
				}
				socket.close();
				area.setCaretPosition (area.getDocument().getLength());
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        

    }
   public static void main(String[] arg){
       new Server();
   }
   
   
   
   public static boolean checkInet() {
    Boolean flag = false;
    HttpURLConnection site = null;
    try {
        site = (HttpURLConnection) new URL("http://www.google.com").openConnection();
        flag = (site.getResponseCode() == HttpURLConnection.HTTP_OK);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (site != null) {
            try {
                site.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    return flag;
}
}
