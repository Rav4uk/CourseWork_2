import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    Server(){ // интерфейс сервера
        JFrame main_window = new JFrame("Server"); //создается основное окно
        main_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_window.setSize(200, 300); 
		//создаем зону для текста
        area = new JTextArea();
		JScrollPane scroll = new JScrollPane(area);
		main_window.getContentPane().add(scroll);
		area.setEditable(false);
        //main_window.add(area);
        main_window.setVisible(true);
        connect();
        
    }
   
    public void connect(){
        int port = 5555;
		String answer;
		
		area.setText(null);
        //try - пробуем сделать; связан с catch (ниже)
        try {
            ServerSocket ss = new ServerSocket(port);
            area.append ("Local IP address:\n" + 
            InetAddress.getLocalHost().toString() + "\n\n");
			int r=0;
			
            while(true){
				//area.setText(null); //очистка старого
				//создаем сокет для подключения
				area.append("Waiting for connection\n");
                Socket socket = ss.accept();
				area.append("Connected\n");
				//создаем входной поток и поток входных данных
                InputStream in = socket.getInputStream();
				byte[] str = new byte[64 * 1024];
				r = in.read(str);
				String data = new String(str, 0, r);
                area.append("Client's message: "+data+"\n");
				
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
						if (CheckProcess("ffmpeg.exe"))
						{
							Runtime.getRuntime().exec("kill.bat");
							answer = "Stream's stopped";	
							area.append("Stream's stopped\n");
						}
						else
						{
							answer = "There is no stream";
							area.append("There is no stream\n");
						}
						socket.getOutputStream().write(answer.getBytes());
					}
					else
					{
						if (!CheckProcess("ffmpeg.exe"))
						{
							try {
								String prog_name = "stream"+data+".bat";
								Runtime.getRuntime().exec(prog_name);
								answer="Stream's started (" + data + ")";
								area.append("Stream's started (" + data + ")\n");
							} catch (Exception e) {
								answer="Error in creating stream";
								area.append("Error in creating stream\n");
								System.out.println(e.getMessage());
							}	
							
						
						}
						else
							answer="Stream's already started";
						socket.getOutputStream().write(answer.getBytes());
					}
				}
				socket.close();
				area.setCaretPosition (area.getDocument().getLength());
            }
        }
		//вывод ошибки если что-то пошло не так
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
   StringBuilder fullprocess = new StringBuilder();
   public boolean CheckProcess(String CmdProcess)
   {
	try {
		Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = input.readLine()) != null) 
		{
	    		fullprocess.append(line).append("\n");
		}
		input.close();
	} catch (IOException e) {}
	String process = fullprocess.toString();
	if(process.contains(CmdProcess))
		return true;
	else
		return false;
   }
	   
}
