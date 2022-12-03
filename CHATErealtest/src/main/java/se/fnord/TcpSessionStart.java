package se.fnord;

import se.fnord.internal.Util;


public class TcpSessionStart implements TcpSessionEvent {
	private final TcpSessionId key;
	private final int index;

	public TcpSessionStart(TcpSessionId key, int index) {
		this.key = key;
		this.index = index;
	}

	@Override
	public TcpSessionId session() {
		return key;
	}

	@Override
	public Direction direction() {
		return Direction.NONE;
	}

	@Override
	public String toString() {
		return String.format("<session start %s:%d->%s:%d>", Util.toDottedQuad(key.clientAddress()), key.clientPort(),
		    Util.toDottedQuad(key.serverAddress()), key.serverPort());
	}

	@Override
	public int index() {
		return index;
	}
}
