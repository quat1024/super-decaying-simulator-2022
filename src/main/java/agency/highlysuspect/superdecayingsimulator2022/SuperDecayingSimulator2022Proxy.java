package agency.highlysuspect.superdecayingsimulator2022;

public interface SuperDecayingSimulator2022Proxy {
	default void initalize() {};
	
	class Server implements SuperDecayingSimulator2022Proxy {}
}
