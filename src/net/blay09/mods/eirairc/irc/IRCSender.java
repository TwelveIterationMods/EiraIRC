package net.blay09.mods.eirairc.irc;

import net.blay09.mods.eirairc.api.IRCConnection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by Blay09 on 13.08.2014.
 */
public class IRCSender implements Runnable {

	private static final String LINE_FEED = "\r\n";

	private final Thread thread;
	private final LinkedList<String> queue = new LinkedList<String>();
	private boolean running;
	private BufferedWriter writer;

	public IRCSender() {
		thread = new Thread(this);
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	public void start() {
		running = true;
		thread.start();
	}

	public boolean addToSendQueue(String message) {
		synchronized (queue) {
			queue.addLast(message);
		}
		return true;
	}

	@Override
	public void run() {
		try {
			while(running) {
				synchronized (queue) {
					if(queue.size() > 0) {
						while(!queue.isEmpty()) {
							String entry = queue.removeFirst();
							writer.write(entry);
							writer.write(LINE_FEED);
						}
						writer.flush();
					}
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) {}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException ignored) {
		}
	}
}
