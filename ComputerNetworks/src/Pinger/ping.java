package Pinger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ping {

	public static final int repeat = 2;
	public static final String[][] ipAddresses = { { "TR Central/Ankara", "5.23.0.17" },
			{ "TR Central/Istanbul", "46.34.64.255" }, { "AP NorthEast/Tokyo", "3.112.0.0" },
			{ "AP SouthEast/Singapore", "3.0.0.9" }, { "CA Central/Ontario", "15.222.0.0" },
			{ "CH North/Beijing", "54.222.240.4" }, { "EU Central/Frankfurt", "3.120.0.0" },
			{ "EU North/Stockholm", "13.48.0.0" }, { "EU West/Dublin", "18.202.0.0" },
			{ "ME South/Manama", "15.185.32.254" }, { "SA East/São Paulo", "18.229.0.0" },
			{ "US East/Virginia", "3.80.0.0" } };
	public static final int arrSize = ipAddresses.length;
	public static String[] averageRTT = new String[arrSize];
	public static File[] files = new File[arrSize];

	public static long pingIt(String ipAddress) {
		try {
			InetAddress inet = InetAddress.getByName(ipAddress);

			// System.out.println("Sending Ping Request to " + Geolocation + "(" + ipAddress
			// +")");

			long finish = 0;
			long start = new GregorianCalendar().getTimeInMillis();

			if (inet.isReachable(5000)) {
				finish = new GregorianCalendar().getTimeInMillis();
				// System.out.println("Ping RTT: " + (finish - start + "ms"));
			} else {
				finish = 5000;
				start = 0;
			}

			return (long) (finish - start);
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
			return 0;
		}
	}

	public static String FindTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	public static long calculateAvg() throws IOException {
		long avgMS;

		for (int i = 0; i < arrSize; i++) {
			avgMS = 0;
			for (int j = 0; j < repeat; j++) {
				avgMS += pingIt(ipAddresses[i][1]);
			}

			averageRTT[i] = Long.toString(avgMS / repeat);

			FileWriter fileWriter = new FileWriter(files[i], true);
			fileWriter.write(MessageFormat.format("Average Ping RTT to {0} ({1}) is {2} at {3}\n", ipAddresses[i][0],
					ipAddresses[i][1], averageRTT[i], FindTime()));
			fileWriter.close();
		}

		return 0;
	}

	public static void main(String[] args) throws IOException {
		for (int i = 0; i < arrSize; i++) {
			files[i] = new File("./" + ipAddresses[i][1] + ".txt");
			files[i].createNewFile();
		}

		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		ses.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					calculateAvg();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("---");
			}
		}, 0, 4, TimeUnit.HOURS);
	}

}
