package com.thundertouch.server.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import com.thundertouch.server.http.listener.ContextListener;
import com.thundertouch.server.tcp.StatusServer;
import com.thundertouch.utils.Logger;

@SuppressWarnings("serial")
public class ThunderTouchServlet extends HttpServlet {

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		StatusServer tcpServer = ContextListener.tcpServer;
		if (req.getParameter("client") != null) {
			Socket cs = tcpServer.clients.get(req.getParameter("client"));
			if (cs != null) {
				tcpServer.write(cs.getInetAddress().getHostAddress(), cs
						.getPort());
			}
		}
		Iterator itr = tcpServer.clients.entrySet().iterator();
		String html = "<select id='clientList'>";
		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry) itr.next();
			String key = (String) entry.getKey();
			html += "<option value='" + key + "'>" + key + "</option>";
		}
		html += "</select>";
		resp.setContentType("text/html");
		resp.getWriter().print(html);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

}