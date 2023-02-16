package se.skltp.tak.web.util;

import se.skltp.tak.core.entity.*;

import java.util.List;

/**
 * // Used for data crunching and handling during checks and model pushes.
 */
public class PublishDataWrapper {

  // DATA CONTAINERS

  public List<AnropsAdress> anropsAdressList;
  public List<Anropsbehorighet> anropsbehorighetList;
  public List<Filtercategorization> filtercategorizationList;
  public List<Filter> filterList;
  public List<LogiskAdress> logiskAdressList;
  public List<RivTaProfil> rivTaProfilList;
  public List<Tjanstekomponent> tjanstekomponentList;
  public List<Tjanstekontrakt > tjanstekontraktList;
  public List<Vagval> vagvalList;

  public enum ScanModeUsed {
    NONE,
    PendingEntriesForAllUsers,
    EntriesBelongingToPubVer,
    PendingEntriesForUsername
  }
  public ScanModeUsed scanModeUsed = ScanModeUsed.NONE;

  // CONSTRUCTOR
  public PublishDataWrapper() {}

  ////
  // SCAN TEST
  ////

  public boolean UsernameHasNoEntryAmongData(String loggedInUser) {
    if (scanModeUsed == ScanModeUsed.NONE) {
      throw new IllegalStateException(
          "PublishDataWrapper is attempting to run a test on its data container before having " +
              "retrieved data for them.");
    }
    for (AnropsAdress entry: anropsAdressList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
    for (Anropsbehorighet entry: anropsbehorighetList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
    for (Filtercategorization entry: filtercategorizationList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
    for (Filter entry: filterList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
    for (LogiskAdress entry: logiskAdressList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
    for (RivTaProfil entry: rivTaProfilList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
    for (Tjanstekomponent entry: tjanstekomponentList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
    for (Tjanstekontrakt entry: tjanstekontraktList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
    for (Vagval entry: vagvalList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;

    // Else, fallback, found no matches.
    return true;
  }
}
