package com.thundertouch.server.http.listener;

import java.io.IOException;
import javax.servlet.*;
import com.thundertouch.server.tcp.StatusServer;

public class ContextListener implements ServletContextListener {
	
	public static StatusServer tcpServer;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		try {
			if (!StatusServer.isListening) {
				tcpServer = new StatusServer();
				tcpServer.initServer(8888);
				tcpServer.listen();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}