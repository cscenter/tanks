package tanks.model;

import java.util.*;
import java.io.*;

public class GameModelReader {
    public static void parse(GameModel model, String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename)))
		{
			String sCurrentLine;
            sCurrentLine = br.readLine();
            String[] dimensions = sCurrentLine.split(" ");
            
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);

            model.rebuild(width, height);
            
            char letter;
            for (int i = 0; i < height; ++i) {
			    sCurrentLine = br.readLine();
			    String[] tags = sCurrentLine.split(" ");
			    
			    for (int j = 0; j < width; ++j)	{
			        switch (MapLegend.getMapLegend(tags[j])) {
			        case WATER:
			            letter = 'W';
			            break;
			        case WOODENWALL:
			            letter = 'O';
			            break;
			        case STONEWALL:
			            letter = 'S';
			            break;
			        case ASPHALT:
			            letter = 'A';
			            break;
			        default: ////// ERROR
			            letter = 'E';
			            break;
			        }

			        if (letter != 'A') {
    			        model.addImmovableObject(i, j, letter);
			        }
			    }
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
