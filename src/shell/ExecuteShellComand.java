package shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecuteShellComand {

	public Command executeCommand(String cmd) {

		StringBuffer output = new StringBuffer();
		Command command = new Command();
		command.setCmd(cmd);
		Process p;
		try {
			p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
			command.setExitValue(p.exitValue());
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		command.setOutput(output.toString());

		return command;

	}

}