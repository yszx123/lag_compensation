package otechniques.network;

import java.util.concurrent.ConcurrentLinkedQueue;

import otechniques.network.packets.ControlPacket;
import otechniques.network.packets.Packet;

public abstract class PacketManager {
	protected ConcurrentLinkedQueue<Packet> receivedPackets = new ConcurrentLinkedQueue<>();
	protected ConcurrentLinkedQueue<ControlPacket> receivedControlPackets = new ConcurrentLinkedQueue<>();

	protected ConcurrentLinkedQueue<Packet> packetSendingQueue = new ConcurrentLinkedQueue<>();
	protected ConcurrentLinkedQueue<ControlPacket> controlPacketSendingQueue = new ConcurrentLinkedQueue<>();

	public ConcurrentLinkedQueue<ControlPacket> getReceivedControlPackets() {
		return receivedControlPackets;
	}

	public void addControlPacketToSend(ControlPacket packet) {
		controlPacketSendingQueue.add(packet);
	}
	
	public void addReceivedControlPacket(ControlPacket packet) {
		receivedControlPackets.add(packet);
	}

	public void addPacket(Packet p) {
		packetSendingQueue.add(p);
	}

	public ConcurrentLinkedQueue<Packet> getReceivedPackets() {
		return receivedPackets;
	}
}
