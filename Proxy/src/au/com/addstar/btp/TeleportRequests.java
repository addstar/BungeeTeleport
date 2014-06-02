package au.com.addstar.btp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TeleportRequests {
	private Map<String, TeleportRequest> requests;
	
	public TeleportRequests() {
		requests = new HashMap<String, TeleportRequest>();
	}
	
	static class TeleportRequest {
		public String getSrcPlayer() {
			return SrcPlayer;
		}
		public void setSrcPlayer(String srcPlayer) {
			SrcPlayer = srcPlayer;
		}
		public String getDstPlayer() {
			return DstPlayer;
		}
		public void setDstPlayer(String dstPlayer) {
			DstPlayer = dstPlayer;
		}
		public int getDelay() {
			return Delay;
		}
		public void setDelay(int delay) {
			Delay = delay;
		}
		public int getExpiry() {
			return Expiry;
		}
		public void setExpiry(int expiry) {
			Expiry = expiry;
		}
		public Date getRequestTime() {
			return RequestTime;
		}
		public void setRequestTime(Date requestTime) {
			RequestTime = requestTime;
		}
		private String SrcPlayer;
		private String DstPlayer;
		private int Delay;
		private int Expiry;
		private Date RequestTime;
	}
	
	public boolean hasActiveRequest(String p) {
		if (requests.get(p) != null) {
			return true;
		}
		return false;
	}
	
	public boolean StoreRequest(TeleportRequest tpr) {
		String p = tpr.getDstPlayer();
		requests.put(p, tpr);
		return true;
	}
}
