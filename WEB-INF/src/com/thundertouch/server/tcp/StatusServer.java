package com.thundertouch.server.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.thundertouch.utils.Logger;

public class StatusServer implements Runnable {
	// ͨ��������
	private Selector selector;
	private int port;
	public static boolean isListening = false;
	public Map<String, Socket> clients;
	
	/**
	 * ���һ��ServerSocketͨ�������Ը�ͨ����һЩ��ʼ���Ĺ���
	 * 
	 * @param port
	 *            �󶨵Ķ˿ں�
	 * @throws IOException
	 */
	public void initServer(int port) throws IOException {
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		this.port = port;
		this.selector = Selector.open();
		this.clients = new HashMap<String, Socket>();
		serverChannel.socket().bind(new InetSocketAddress(port));

		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * ����selector
	 * 
	 * @throws IOException
	 */
	public void listen() throws IOException {
		Logger.write("server listened on " + this.port);
		new Thread(this).start();
		isListening = true;
	}

	/**
	 * ��ѯselector
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (true) {
			// TODO Auto-generated method stub
			try {
				selector.select();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Iterator ite = selector.selectedKeys().iterator();
			while (ite.hasNext()) {
				SelectionKey key = (SelectionKey) ite.next();
				ite.remove();
				try {
					if (key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel channel = server.accept();
						channel.configureBlocking(false);
						channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

						Logger.write("client connected ----------->"
								+ channel.socket().getInetAddress()
										.getHostAddress() + ":"
								+ channel.socket().getPort());

						// TO DO д���ݿ⣬ע���豸��Ϣ

					} else if (key.isReadable()) {
						read(key);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
					key.cancel();
					try {
						key.channel().close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * �����ȡ�ͻ��˷�������Ϣ ���¼�
	 * 
	 * @param key
	 * @throws IOException
	 */
	public void read(SelectionKey key) throws IOException {
		// �������ɶ�ȡ��Ϣ:�õ��¼�������Socketͨ��
		SocketChannel channel = (SocketChannel) key.channel();
		// ������ȡ�Ļ�����
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		channel.read(buffer);
		byte[] data = buffer.array();
		String name = new String(data).trim();
		if(name != null) clients.put(name, channel.socket());
		Logger.write("receive from client��" + name);
	}

	/**
	 * ��ĳ���׽���д����
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void write(String host, int port) throws IOException {
		synchronized (selector) {
			// TODO Auto-generated method stub
			selector.select();
			Iterator ite = selector.selectedKeys().iterator();
			while (ite.hasNext()) {
				SelectionKey key = (SelectionKey) ite.next();
				ite.remove();
				if(key.isWritable()){
					SocketChannel channel = (SocketChannel)key.channel();
					Socket cs = channel.socket();
					if(host.equals(cs.getInetAddress().getHostAddress()) && cs.getPort() == port){
						Logger.write("write to ------------>" + host + ":" + port);
						channel.write(ByteBuffer.wrap(new String("i'm server").getBytes()));
					}
				}
			}
		}
	}
	
}