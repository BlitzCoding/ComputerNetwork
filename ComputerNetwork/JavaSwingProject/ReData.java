import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class ReData {

	Socket receiver2, receiver22;
	ServerSocket receiverData, receiverData2;
	Socket connection = null;
	ObjectOutputStream out;
	ObjectInputStream in;
	String msg;
	int count = 0;
	String result = "";
	Scanner sc = new Scanner(System.in);
	final int n = 1;
	String stuffing = "";
	int k = 0;
	Random rand = new Random();

	public void run() {
		try {
			System.out.println("-------------Receiver-------------\n");
			System.out.println("=============Datalink Layer=============\n");
			receiverData = new ServerSocket(1050);
			connection = receiverData.accept();
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			msg = (String) in.readObject();

			System.out.println("Physical Layer에서 " + msg + " 전달받음");

			System.out.println("Bit-unstuffing 중...");

			for (int i = 0; i < msg.length(); i++) {
				if (msg.charAt(i) == '1') {
					count++;
					result = result + msg.charAt(i);
				} else {
					count = 0;
					result = result + msg.charAt(i);
				}
				if (count == 5) {
					if ((i + 2) != result.length()) {
						result = result + msg.charAt(i + 2);
					} else {
						result = result + '1';
					}
					i = i + 2;
					count = 1;
				}

			}
			Thread.sleep(2000);
			System.out.println("Unstuffed bit 완료 : " + result);

			System.out.println("Network layer으로 전송중...");

			receiver2 = new Socket("localhost", 1060);
			out = new ObjectOutputStream(receiver2.getOutputStream());
			out.flush();
			in = new ObjectInputStream(receiver2.getInputStream());
			Thread.sleep(2000);
			out.writeObject(result);
			System.out.println("Network layer에 전송완료\n");
			System.out.println("===========================================\n");

			receiverData2 = new ServerSocket(1110);
			connection = receiverData2.accept();
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			msg = (String) in.readObject();

			System.out.println("Network Layer에서 " + msg + "전달받음, Bit-Stuffing 적용중...");

			for (int i = 0; i < msg.length(); i++) {
				if (msg.charAt(i) == '1') {
					count++;
					stuffing = stuffing + msg.charAt(i);
				} else {
					stuffing = stuffing + msg.charAt(i);
					count = 0;
				}

				if (count == 5) {
					stuffing = stuffing + '0';
					count = 0;
				}
			}
			Thread.sleep(2000);
			System.out.println("Stuffed bit : " + stuffing + " 로 변환 완료\n");
			System.out.println("▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒CSMA/CD 실행▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒");

			System.out.print("1이상의 Maxium Propagation time값을 입력하세요 : ");
			int Tp = sc.nextInt();
			if (Tp < 1) {
				System.out.println("1미만의 수 입력, 프로그램 종료합니다");
				System.exit(0);
			}
			System.out.println();

			while (k < 15) {
				System.out.printf("***현재 k = %d, Channel idle or busy 여부 확인***", k);
				System.out.println();
				while (n < 2) {
					int y = rand.nextInt(2);
					try {
						if (y == 1) {
							System.out.println("Channel is busy!");
							Thread.sleep(1000);
						} else {
							System.out.println("Channel is idle");
							System.out.println();
							Thread.sleep(1000);
							break;
						}
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
				System.out.println("***Transmit and receive 수행중***");
				try {
					System.out.println("전송완료");
					System.out.println();
					Thread.sleep(1000);

					System.out.println("**Collison 여부 확인중**");
					Thread.sleep(1000);
				} catch (InterruptedException e) {

					e.printStackTrace();

				}

				try {
					int z = rand.nextInt(2);
					if (z == 0) {
						System.out.println("Collison 없음!, 전송 Success\n");
						Thread.sleep(1000);
						break;
					} else {
						System.out.println("Collison 감지!, Send a jamming signal");
						System.out.println();
						Thread.sleep(1000);
						k++;

						if (k > 15) {
							System.out.println("시도 횟수 15번 초과, 통신 실패 Abort");
							break;
						}
					}
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

				try {
					Thread.sleep(1000);
					int rk = (int) Math.pow(2, k);
					System.out.println("***현재 R의 값은 0 ~ " + (rk - 1) + "입니다.***");
					Thread.sleep(1000);
					int ran = rand.nextInt(rk);

					System.out.println("***선택된 R의 값 : " + ran + ", Tb의 값은 " + (ran * Tp) + "입니다.***");
					System.out.println();
					int back = ran * Tp;
					if (back == 0) {
						System.out.println("0이므로 즉시 전송합니다");
						System.out.println();
					} else {
						System.out.println("대기 시간 : " + back);
						System.out.println();
						Thread.sleep(back);
					}
				} catch (InterruptedException e) {

					e.printStackTrace();

				}

			}

			receiver22 = new Socket("localhost", 1120);
			out = new ObjectOutputStream(receiver22.getOutputStream());
			out.flush();
			in = new ObjectInputStream(receiver22.getInputStream());

			System.out.println("CSMA/CD 완료, Physical Layer로 메시지 보내는중...");
			Thread.sleep(2000);
			out.writeObject(stuffing);
			System.out.println("Physical Layer로 " + stuffing + " 전송 완료");

			System.out.println();
			System.out.println("===========================================");

		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
		ReData s = new ReData();
		s.run();

	}

}
