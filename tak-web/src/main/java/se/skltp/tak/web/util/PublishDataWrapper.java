package se.skltp.tak.web.util;

import se.skltp.tak.core.entity.*;

import java.util.ArrayList;
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
    PENDING_ENTRIES_FOR_ALL_USERS,
    ENTRIES_FOR_PUBVERSION,
    PENDING_ENTRIES_FOR_USERNAME
  }
  public ScanModeUsed scanModeUsed = ScanModeUsed.NONE;

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

  // Data checks.
  public void QualityAndSanityCheck(String username) {
    // Check that the current user actually has anything to publish.
    CheckIfUserHasChangesToPublish(this, username);

    // Below section scans all entries and their referenced sub-entries, to ensure that they have the same editor.
    // The below approach is improved from the prior TAK implementation,
    //   wherein this one will scan for and list all such errors,
    //   whereas the prior one would stop and report on first found error.
    // Below code has, just as the prior did, the loop-hole however that if one user makes two consecutive TAK-edits,
    //  then there could still be mistaken mappings, even though the updated-by-username is the same on both entities.
    List<String> errorLines = new ArrayList<>();
    checkAnropsAdressReferences(this, errorLines);
    checkVagvalReferences(this, errorLines);
    checkAnropsbehorighetReferences(this, errorLines);
    checkFilterCategorizationReferences(this, errorLines);

    if (errorLines.size() > 0) {
      String excMsg = "There were mismatches in editing users between previewed data entries and their sub-entries. List below:\n" +
          String.join("\n", errorLines);
      throw new IllegalStateException (excMsg);
    }
  }

  public List<String> getChangeReport() {
    List<String> report = new ArrayList<>();
    appendChangesToReport(report, "Anropsadresser:", anropsAdressList);
    appendChangesToReport(report, "Anropsbehörigheter:", anropsbehorighetList);
    appendChangesToReport(report, "Filterkategorier:", filtercategorizationList);
    appendChangesToReport(report, "Filter:", filterList);
    appendChangesToReport(report, "Logiska adresser:", logiskAdressList);
    appendChangesToReport(report, "RIV-TA-profiler:", rivTaProfilList);
    appendChangesToReport(report, "Tjänstekomponenter:", tjanstekomponentList);
    appendChangesToReport(report, "Tjänstekontrakt:", tjanstekontraktList);
    appendChangesToReport(report, "Vägval:", vagvalList);
    return report;
  }

  private void CheckIfUserHasChangesToPublish(PublishDataWrapper publishData, String username) {
    if (publishData.UsernameHasNoEntryAmongData(username)) {
      throw new IllegalStateException("Quality check before allowing publishing to occur found that current user has no pending items to publish.");
    }
  }

  private void checkAnropsAdressReferences(PublishDataWrapper publishData, List<String> errorLines) {
    for (AnropsAdress adr : publishData.anropsAdressList) {
      CheckMatchingEditors(adr, adr.getTjanstekomponent(), errorLines);
      CheckMatchingEditors(adr, adr.getRivTaProfil(), errorLines);
    }
  }
  private void checkVagvalReferences(PublishDataWrapper publishData, List<String> errorLines) {
    for (Vagval vv : publishData.vagvalList) {
      CheckMatchingEditors(vv, vv.getTjanstekontrakt(), errorLines);
      CheckMatchingEditors(vv, vv.getLogiskAdress(), errorLines);
      CheckMatchingEditors(vv, vv.getAnropsAdress(), errorLines);
    }
  }
  private void checkAnropsbehorighetReferences(PublishDataWrapper publishData, List<String> errorLines) {
    for (Anropsbehorighet ab : publishData.anropsbehorighetList) {
      CheckMatchingEditors(ab, ab.getTjanstekontrakt(), errorLines);
      CheckMatchingEditors(ab, ab.getLogiskAdress(), errorLines);
      CheckMatchingEditors(ab, ab.getTjanstekonsument(), errorLines);
    }
  }
  private void checkFilterCategorizationReferences(PublishDataWrapper publishData, List<String> errorLines) {
    for (Filtercategorization fc : publishData.filtercategorizationList) {
      CheckMatchingEditors(fc, fc.getFilter(), errorLines);
    }
  }

  // Type combos, to extract pretty-printed error meta.
  private void CheckMatchingEditors(AnropsAdress aa, Tjanstekomponent tk, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(aa, tk)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, aa.getUpdatedBy(), tk.getUpdatedBy(),
          "Anropsadress:" + aa.getAdress(), "Tjänstekonsument:" + tk.getHsaId());
    }
  }
  private void CheckMatchingEditors(AnropsAdress aa, RivTaProfil rp, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(aa, rp)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, aa.getUpdatedBy(), rp.getUpdatedBy(),
          "Anropsadress:" + aa.getAdress(), "RivTaProfil:" + rp.getNamn());
    }
  }
  private void CheckMatchingEditors(Vagval vv, Tjanstekontrakt tk, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(vv, tk)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, vv.getUpdatedBy(), tk.getUpdatedBy(),
          "Vägval:" + vv.getId(), "Tjänstekontrakt:" + tk.getNamnrymd());
    }
  }
  private void CheckMatchingEditors(Vagval vv, LogiskAdress la, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(vv, la)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, vv.getUpdatedBy(), la.getUpdatedBy(),
          "Vägval:" + vv.getId(), "LogiskAdress:" + la.getHsaId());
    }
  }
  private void CheckMatchingEditors(Vagval vv, AnropsAdress aa, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(vv, aa)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, vv.getUpdatedBy(), aa.getUpdatedBy(),
          "Vägval:" + vv.getId(), "AnropsAdress:" + aa.getAdress());
    }
  }

  private void CheckMatchingEditors(Anropsbehorighet ab, Tjanstekontrakt tk, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(ab, tk)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, ab.getUpdatedBy(), tk.getUpdatedBy(),
          "Anropsbehorighet:" + ab.getIntegrationsavtal(), "Tjänstekontrakt:" + tk.getNamnrymd());
    }
  }
  private void CheckMatchingEditors(Anropsbehorighet ab, LogiskAdress tk, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(ab, tk)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, ab.getUpdatedBy(), tk.getUpdatedBy(),
          "Anropsbehorighet:" + ab.getIntegrationsavtal(), "LogiskAdress:" + tk.getHsaId());
    }
  }

  private void CheckMatchingEditors(Anropsbehorighet ab, Tjanstekomponent tk, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(ab, tk)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, ab.getUpdatedBy(), tk.getUpdatedBy(),
          "Anropsbehorighet:" + ab.getIntegrationsavtal(), "Tjänstekomponent:" + tk.getHsaId());
    }
  }
  private void CheckMatchingEditors(Filtercategorization fc, Filter f, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(fc, f)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, fc.getUpdatedBy(), f.getUpdatedBy(),
          "FilterKategorisering:" + fc.getCategory(), "Filter:" + f.getServicedomain());
    }
  }


  private boolean AbstractVersionInfoEditorsMismatch(AbstractVersionInfo avi1,
                                                     AbstractVersionInfo avi2) {
    String publishingUser = avi1.getUpdatedBy();
    String otherEntityUser = avi2.getUpdatedBy();

    return publishingUser != null && otherEntityUser != null
        && !publishingUser.equalsIgnoreCase(otherEntityUser);
  }

  // Throwers
  private void RecordErrorWhenEntityEditorsMismatch(List<String> errorLines,
                                                    String entityUser, String otherEntityUser,
                                                    String entityPrint, String otherEntityPrint) {
    String output =
        "ERROR: Mismatch users:" +
            " Entity User: " + entityUser +
            " Sub-entity user: " + otherEntityUser +
            " Entity details: " + entityPrint +
            " Sub-entity details: " + otherEntityPrint;

    errorLines.add(output);
  }

  private void appendChangesToReport(List<String> report, String header, List entries) {
    if (entries == null || entries.size() == 0) return;
    report.add(header);
    for(Object entry : entries) {
      String action = ((AbstractVersionInfo)entry).isDeletedInPublishedVersion() ? "Borttagen" : "Skapad/uppdaterad";
      report.add(String.format("%s - %s", action, entry));
    }
  }
}
