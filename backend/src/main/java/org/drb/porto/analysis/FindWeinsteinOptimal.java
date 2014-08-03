package org.drb.porto.analysis;


import org.drb.porto.base.Grade;



/**
 * 
 * Attempts to detect a buy signal
 * 
 * 
 *   todo:
 *      - compute Mansfield's relative price               => ok
 *      - start assigning a note and check results         => ok
 *      - create abstract class "Grade" (PrintHeader( ), PrintDetails( )) and subclass it for Weinstein
 *      
 *      - select the best choices on a year started in the period 2005/09-2005/11
 *           - date d1 = date of selection
 *           - date d2 = reaches maximum over the period
 *           -> performance = q(d2)-q(d1)/(d2-d1)
 *      - optimize parameter to get as many of those stocks as possible in selection
 *      - repeat exercise 1 year earlier, then 1 year earlier
 *      - full loop
 *       
 * Best note:
 * 
 *   - phase detection -> phase 1 or 2
 *   - above MM (mandatory)
 *   - MM slope >0 
 *   - volume increase (to compare with global average)
 *   - (approximate) cross of resistance
 *
 *  Note needs to be comparable to other stocks;
 *  no buy advice if note < absolute threshold (to calibrate on the past) 
 *
 *
 *
 *       <--       m_nMAvgDays         -->|<--   m_nNbDaysBeforePhase2    -->|<--  m_nNbDaysAboveMA  -->  
 *                                        |       Phase 1                    |         Phase 2   
 *         Additional history to get      |   Stocks can be                  |    Stocks MUST be
 *         a correct computation of MA    |   < MA during                    |   > MA during
 *                                        |   this period                    | 
 *
 */
class FindWeinsteinOptimal extends FindOptimal
{

   // tries to find stocks for which:
   // % of days above MM was < 50 % last month
   // % of days above MM has been = 100 % for the past 2 weeks
   public Grade CreateGrade( )
   {
      return new WeinsteinGrade( );
   }

}

