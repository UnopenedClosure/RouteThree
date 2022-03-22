package routethree;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.ini4j.InvalidFileFormatException;

import org.junit.jupiter.api.Test;

public class TestMain {

	@Test
	public void validateProjectSetup() throws InvalidFileFormatException, IOException {
		Main.main(new String[] {});
		
		File fExpected = new File("./test/routethree/out_frlg_clefable_frlgr2_tas_expected.txt");
		File fActual = new File("./outputs/out_frlg_clefable_frlgr2_tas.txt");
		
		//assertTrue(fExpected.exists());//sanity check to ensure project is set up correctly
		assertTrue(fActual.exists());
		
		assertTrue(FileUtils.contentEquals(fExpected, fActual));
	}
}
