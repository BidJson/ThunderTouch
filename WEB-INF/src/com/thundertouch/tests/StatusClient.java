package com.thundertouch.tests;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NIO�ͻ���
 * 
 * @author С·
 */
public class StatusClient implements Runnable {
	// ͨ��������
	private Selector selector;
	private String name;

	/**
	 * ���һ��Socketͨ�������Ը�ͨ����һЩ��ʼ���Ĺ���
	 * 
	 * @param ip
	 *            ���ӵķ�������ip
	 * @param port
	 *            ���ӵķ������Ķ˿ں�
	 * @throws IOException
	 */
	public void initClient(String ip, int port, String name) throws IOException {
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		this.selector = Selector.open();

		// �ͻ������ӷ�����,��ʵ����ִ�в�û��ʵ�����ӣ���Ҫ��listen���������е�
		// ��channel.finishConnect();�����������
		channel.connect(new InetSocketAddress(ip, port));

		channel.register(selector, SelectionKey.OP_CONNECT);
		this.name = name;
	}

	/**
	 * �����ȡ����˷�������Ϣ ���¼�
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
		String msg = new String(data).trim();
		System.out.println("[" + this.name + "]received from server��" + msg);
	}

	/**
	 * �����ͻ��˲���
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		for (int i = 0; i < 10; i++) {
			StatusClient client = new StatusClient();
			client.initClient("localhost", 8888, "JsonBid[" + i + "]");
			new Thread(client).start();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// ��ѯ����selector
			while (true) {
				// ѡ��һ����Խ���I/O�������¼�������selector��,�ͻ��˵ĸ÷�������������
				// ����ͷ���˵ķ�����һ�����鿴apiע�Ϳ���֪����������һ��ͨ����ѡ��ʱ��
				// selector��wakeup���������ã��������أ������ڿͻ�����˵��ͨ��һֱ�Ǳ�ѡ�е�
				selector.select();
				// ���selector��ѡ�е���ĵ�����
				Iterator ite = this.selector.selectedKeys().iterator();
				while (ite.hasNext()) {
					SelectionKey key = (SelectionKey) ite.next();
					ite.remove();
					if (key.isConnectable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						// ����������ӣ����������
						if (channel.isConnectionPending()) {
							channel.finishConnect();
						}
						channel.configureBlocking(false);

						channel.write(ByteBuffer
								.wrap(new String(this.name).getBytes()));
						channel.register(this.selector, SelectionKey.OP_READ);
					} else if (key.isReadable()) {
						read(key);
					}
				}

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}