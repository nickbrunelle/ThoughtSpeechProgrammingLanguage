import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class PrettyPrinter {

	public static void main(String[] args) {
		try {
			File file = new File(args[0]); 
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
			fileReader.close();

			String prettystring;
			prettystring = stringBuffer.toString();

			try(PrintWriter out = new PrintWriter("pretty.brun")){
				out.println(prettystring);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}