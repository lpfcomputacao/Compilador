package compilador;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LoadFile {
	
    public InputStream file;
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    
    public LoadFile(String fileName) {
        try {
            file = new FileInputStream(new File(fileName));
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);
            
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

	public ArrayList<String> getFileFeatures() throws IOException {
		ArrayList<String> keywordsList = new ArrayList<>();
		String[] keywordsArray;
		String line;
		String stringResult = new String();
		
		while((line = bufferedReader.readLine()) != null){
			stringResult += line;
		}

		keywordsArray = stringResult.split("'");
		
		for (int i = 0; i < keywordsArray.length; i++) {
			keywordsList.add(keywordsArray[i]);
		}
		return keywordsList;
	}

	public String getStringFile() throws IOException {
		String stringFile = new String();
		String line;
		
		while((line = bufferedReader.readLine()) != null) {
			stringFile+= line;
			stringFile+='\n';
		}
		return stringFile;
	}
}
