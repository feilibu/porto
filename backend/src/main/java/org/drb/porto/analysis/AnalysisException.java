package org.drb.porto.analysis;

public class AnalysisException extends Exception
{
   private static final long serialVersionUID = 1L;

   AnalysisException( String str )
   {
      super( str );
   }
   
   AnalysisException( String str, Throwable e )
   {
      super( str, e );
   }

}
