package com.thundertouch.tests;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NIO客户端
 * 
 * @author 小路
 */
public class StatusClient implements Runnable {
	// 通道管理器
	private Selector selector;
	private String name;

	/**
	 * 获得一个Socket通道，并对该通道做一些初始化的工作
	 * 
	 * @param ip
	 *            连接的服务器的ip
	 * @param port
	 *            连接的服务器的端口号
	 * @throws IOException
	 */
	public void initClient(String ip, int port, String name) throws IOException {
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		this.selector = Selector.open();

		// 客户端连接服务器,其实方法执行并没有实现连接，需要在listen（）方法中调
		// 用channel.finishConnect();才能完成连接
		channel.connect(new InetSocketAddress(ip, port));

		channel.register(selector, SelectionKey.OP_CONNECT);
		this.name = name;
	}

	/**
	 * 处理读取服务端发来的信息 的事件
	 * 
	 * @param key
	 * @throws IOException
	 */
	public void read(SelectionKey key) throws IOException {
		// 服务器可读取消息:得到事件发生的Socket通道
		SocketChannel channel = (SocketChannel) key.channel();
		// 创建读取的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		channel.read(buffer);
		byte[] data = buffer.array();
		String msg = new String(data).trim();
		System.out.println("[" + this.name + "]received from server：" + msg);
	}

	/**
	 * 启动客户端测试
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
			// 轮询访问selector
			while (true) {
				// 选择一组可以进行I/O操作的事件，放在selector中,客户端的该方法不会阻塞，
				// 这里和服务端的方法不一样，查看api注释可以知道，当至少一个通道被选中时，
				// selector的wakeup方法被调用，方法返回，而对于客户端来说，通道一直是被选中的
				selector.select();
				// 获得selector中选中的项的迭代器
				Iterator ite = this.selector.selectedKeys().iterator();
				while (ite.hasNext()) {
					SelectionKey key = (SelectionKey) ite.next();
					ite.remove();
					if (key.isConnectable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						// 如果正在连接，则完成连接
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