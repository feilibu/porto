package org.drb.porto.cmdline;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

public class Main
{
   static private final Logger logger = LoggerFactory.getLogger(Main.class);
   static private Map<String, Command> map = initCommandMap();

   private static Map<String, Command> initCommandMap()
   {
      Map<String, Command> map = new HashMap<String, Command>();
      map.put("populate", new Populate());
      map.put("makeSelection", new MakeSelection());
      map.put("runChart", new RunChart());
      map.put("rectifyTicker", new RectifyTicker());
      return map;
   }

   public static void main(String str[]) throws Exception
   {
      if (str.length == 0)
         System.err.println("Usage: Main <command> [<args>]");
      else
         execCommand(str);
      shutdown();
   }

   private static void shutdown()
   {
      try
      {
         AbandonedConnectionCleanupThread.shutdown();
      }
      catch (Exception e)
      {
         logger.warn("Cannot cleanup MySql thread", e);
      }
   }

   private static void execCommand(String[] str) throws Exception
   {
      String[] cmdArgs = new String[str.length - 1];
      System.arraycopy(str, 1, cmdArgs, 0, cmdArgs.length);
      Command command = map.get(str[0]);
      if (command == null)
         System.err.println("Unknown command;" + str[0]);
      else
         command.run(cmdArgs);
   }
}
