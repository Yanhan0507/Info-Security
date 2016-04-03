import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

public class DataGenerator {
	static Random r = new Random();
	public static void generate(String fileName, long num){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			for(long i = 0; i < num; i++){
				char ch = (char)('a' + r.nextInt(26));	
				bw.write(ch);
			}
			bw.flush();
			bw.close();
		}catch(Exception e){
			
		}

	}
	
	public static void main(String[] args){
		generate("/Users/Yanhan/Desktop/sample1", 10000000);
	}
}
