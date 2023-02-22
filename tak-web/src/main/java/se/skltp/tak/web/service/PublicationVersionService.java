package se.skltp.tak.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.core.memdb.PublishedVersionCache;
import se.skltp.tak.core.util.Util;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.PublicationVersionRepository;
import se.skltp.tak.web.util.PublishDataWrapper;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PublicationVersionService{

  int currentFormatVersion = 1;

  @Autowired
  PublicationVersionRepository PvRepo;

  // SERVICES
  @Autowired RivTaProfilService rivTaProfilService;
  @Autowired TjanstekontraktService tjanstekontraktService;
  @Autowired TjanstekomponentService tjanstekomponentService;
  @Autowired LogiskAdressService logiskAdressService;
  @Autowired AnropsAdressService anropsAdressService;
  @Autowired VagvalService vagvalService;
  @Autowired AnropsBehorighetService anropsBehorighetService;
  @Autowired FilterService filterService;
  @Autowired FilterCategorizationService filterCategorizationService;

  private static final Logger log = LoggerFactory.getLogger(PublicationVersionService.class);

  @Autowired
  public PublicationVersionService(PublicationVersionRepository PvRepo) {
    this.PvRepo = PvRepo;
  }

  public PagedEntityList<PubVersion> getEntityList(Integer offset, Integer max) {
    List<PubVersion> contents = PvRepo.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
        .skip(offset)
        .limit(max)
        .collect(Collectors.toList());
    long total = PvRepo.count();
    return new PagedEntityList<>(contents, (int) total, offset, max);
  }

  public PubVersion findById(Long id) {
    Optional<PubVersion> instance = PvRepo.findById(id);
    if (!instance.isPresent()) {
      throw new IllegalArgumentException("Entity not found");
    }
    return instance.get();
  }

  public PubVersion add(PubVersion newInstance, String username) {
    try {
      log.info("Retrieving currently live PV Snapshot from DB.");
      // Highest ID should be the newest full PubVer snapshot
      PubVersion recentMostPublishedInstance = PvRepo.findTopByOrderByIdDesc();

      log.info("Setting incoming PV metadata.");
      newInstance.setFormatVersion(currentFormatVersion);
      newInstance.setTime(new Date(System.currentTimeMillis()));
      newInstance.setUtforare(username);

      log.info("Persisting incoming PV Instance in DB, which also sets an id value.");
      newInstance = PvRepo.save(newInstance); // TODO: Check for failures?
      // TODO: IF save-fail: render(view: "create", model: [pubVersionInstance: pubVersionInstance]) //(old code from grails.)

      // Merge the data sets.
      log.info("Performing merge of incoming PV data onto current-most snapshot.");
      PubVersion merged = mergeNewPubverDataOnOldPubverSnapshot(
          newInstance,
          recentMostPublishedInstance,
          username);

      log.info("Persisting merged PV to DB, overwriting by id.");
      merged = PvRepo.save(merged); // TODO: Check for failures?
      // TODO: Grails PubVerController L469: Log results of merger; Flash message to frontend; Redirect to created instance;

      log.info(String.format("pubVersion %s created by %s:", merged, username));
      //log.info(merged as JSON);

      log.info("Responding with merged PV to DB.");
      return merged;

    } catch (Exception exc) {
      // TODO: Grails PubVerController L475: Log failure of merger; Flash error message to frontend; Redirect to creation page;
      throw new IllegalStateException(
          "An Exception occurred during the merge of new PubVer data onto recent PubVer snapshot.\n"+
              "Exception body: \n" + exc
      );
    }
  }
  private PubVersion mergeNewPubverDataOnOldPubverSnapshot(
      PubVersion newPubverData,
      PubVersion oldPubverSnapshot,
      String username) throws IOException, SQLException {

    log.debug("MERGING PV DATA.");
    log.debug("Converting old PV Snapshot into a PVCache.");
    PublishedVersionCache pvCache = Util.getPublishedVersionCacheInstance(oldPubverSnapshot);

    log.debug("Populating PVCache with metadata, including id, from new PV.");
    // Populate pvCache with relevant metaData from pending publication.
    pvCache.setTime(newPubverData.getTime());
    pvCache.setUtforare(newPubverData.getUtforare());
    pvCache.setFormatVersion(newPubverData.getFormatVersion());
    pvCache.setKommentar(newPubverData.getKommentar());
    pvCache.setVersion(newPubverData.getId());

    log.debug("Working through per-type additions, updates, and deletions.");
    AddUpdateAndDeleteAllTypesToCache(pvCache, newPubverData.getId(), username);

    log.debug("Serializing pvCache as JSON.");
    String newCacheJSON = Util.fromPublishedVersionToJSON(pvCache);
    log.debug("Compressing JSON String into Serial BLOB");
    Blob blob = new SerialBlob(Util.compress(newCacheJSON));
    log.debug("Storing BLOB and its metadata in merged.");
    newPubverData.setData(blob);
    newPubverData.setStorlek(blob.length());
    log.debug("Returning New PV, containing generated BLOB.");
    return newPubverData;
  }

  public PubVersion update(PubVersion instance) {
    return PvRepo.save(instance);
  }

  ///////
  // SCAN AND POPULATE FUNCTIONS
  ///////

  public PublishDataWrapper ScanForPrePublishedEntries() {
    log.debug("Collecting Pending PV Entries from DB, for ALL Users.");
    PublishDataWrapper result = new PublishDataWrapper();
    result.scanModeUsed = PublishDataWrapper.ScanModeUsed.PENDING_ENTRIES_FOR_ALL_USERS;
    
    result.anropsAdressList = anropsAdressService.findAllByUpdatedByIsNotNull();
    result.anropsbehorighetList = anropsBehorighetService.findAllByUpdatedByIsNotNull();
    result.filtercategorizationList = filterCategorizationService.findAllByUpdatedByIsNotNull();
    result.filterList = filterService.findAllByUpdatedByIsNotNull();
    result.logiskAdressList = logiskAdressService.findAllByUpdatedByIsNotNull();
    result.rivTaProfilList = rivTaProfilService.findAllByUpdatedByIsNotNull();
    result.tjanstekomponentList = tjanstekomponentService.findAllByUpdatedByIsNotNull();
    result.tjanstekontraktList =  tjanstekontraktService.findAllByUpdatedByIsNotNull();
    result.vagvalList = vagvalService.findAllByUpdatedByIsNotNull();
    
    return result;
  }

  public PublishDataWrapper ScanForEntriesAffectedByPubVer(Long pubVerId) {
    log.debug("Collecting PV Entries from DB, relating to PV with provided id.");
    PublishDataWrapper result = new PublishDataWrapper();
    result.scanModeUsed = PublishDataWrapper.ScanModeUsed.ENTRIES_FOR_PUBVERSION;
    
    result.anropsAdressList = anropsAdressService.findAllByPubVersion(pubVerId);
    result.anropsbehorighetList = anropsBehorighetService.findAllByPubVersion(pubVerId);
    result.filtercategorizationList = filterCategorizationService.findAllByPubVersion(pubVerId);
    result.filterList = filterService.findAllByPubVersion(pubVerId);
    result.logiskAdressList = logiskAdressService.findAllByPubVersion(pubVerId);
    result.rivTaProfilList = rivTaProfilService.findAllByPubVersion(pubVerId);
    result.tjanstekomponentList = tjanstekomponentService.findAllByPubVersion(pubVerId);
    result.tjanstekontraktList =  tjanstekontraktService.findAllByPubVersion(pubVerId);
    result.vagvalList = vagvalService.findAllByPubVersion(pubVerId);

    return result;
  }

  public PublishDataWrapper ScanForPendingEntriesByUsername(String username) {
    log.debug("Collecting Pending PV Entries from DB, for current User.");
    PublishDataWrapper result = new PublishDataWrapper();
    result.scanModeUsed = PublishDataWrapper.ScanModeUsed.PENDING_ENTRIES_FOR_USERNAME;
    
    result.anropsAdressList = anropsAdressService.findAllByUpdatedBy(username);
    result.anropsbehorighetList = anropsBehorighetService.findAllByUpdatedBy(username);
    result.filtercategorizationList = filterCategorizationService.findAllByUpdatedBy(username);
    result.filterList = filterService.findAllByUpdatedBy(username);
    result.logiskAdressList = logiskAdressService.findAllByUpdatedBy(username);
    result.rivTaProfilList = rivTaProfilService.findAllByUpdatedBy(username);
    result.tjanstekomponentList = tjanstekomponentService.findAllByUpdatedBy(username);
    result.tjanstekontraktList =  tjanstekontraktService.findAllByUpdatedBy(username);
    result.vagvalList = vagvalService.findAllByUpdatedBy(username);

    return result;
  }


  // ADD UPDATE CRUNCHERS
  // MUST BE RAN AFTER THE ScanForPendingEntriesByUsername FUNCTION.
  public void AddUpdateAndDeleteAllTypesToCache(PublishedVersionCache pvCache, long newPvId, String username) {

    PublishDataWrapper pendingPvEntries = this.ScanForPendingEntriesByUsername(username);

    log.debug("Processing Addition and Updates of PV Entries.");
    // ATTENTION! The order of the sequence is of pivotal importance here.
    // Add and update all object below in correct order
    addUpdateRivTaProfil(pvCache, newPvId, pendingPvEntries.rivTaProfilList);
    addUpdateTjanstekontrakt(pvCache, newPvId, pendingPvEntries.tjanstekontraktList);
    addUpdateTjanstekomponent(pvCache, newPvId, pendingPvEntries.tjanstekomponentList);
    addUpdateLogiskAdress(pvCache, newPvId, pendingPvEntries.logiskAdressList);
    addUpdateAnropsAdress(pvCache, newPvId, pendingPvEntries.anropsAdressList);
    addUpdateVagval(pvCache, newPvId, pendingPvEntries.vagvalList);
    addUpdateAnropsbehorighet(pvCache, newPvId, pendingPvEntries.anropsbehorighetList);
    addUpdateFilter(pvCache, newPvId, pendingPvEntries.filterList);
    addUpdateFiltercategorizations(pvCache, newPvId, pendingPvEntries.filtercategorizationList);

    log.debug("Processing Deletion of PV Entries.");
    // ATTENTION! The order of the sequence is of pivotal importance here.
    // Remove objects in correct order
    deleteFiltercategorizations(pvCache, newPvId, pendingPvEntries.filtercategorizationList);
    deleteFilter(pvCache, newPvId, pendingPvEntries.filterList);
    deleteAnropsbehorighet(pvCache, newPvId, pendingPvEntries.anropsbehorighetList);
    deleteVagval(pvCache, newPvId, pendingPvEntries.vagvalList);
    deleteAnropsAdress(pvCache, newPvId, pendingPvEntries.anropsAdressList);
    deleteLogiskAdress(pvCache, newPvId, pendingPvEntries.logiskAdressList);
    deleteTjanstekomponent(pvCache, newPvId, pendingPvEntries.tjanstekomponentList);
    deleteTjanstekontrakt(pvCache, newPvId, pendingPvEntries.tjanstekontraktList);
    deleteRivTaProfil(pvCache, newPvId, pendingPvEntries.rivTaProfilList);
  }
  
  
  // Patterned Add-Update routines.
  //   TODO: Make Generics?
  //   These might be possible to generalize through some T-type pattern or reflection,
  //   but as some of the lists and containers are types sch optimization has been pushed into the future.
  private void addUpdateRivTaProfil(PublishedVersionCache pvCache, Long newPvId, List<RivTaProfil> pendingList) {
    for (RivTaProfil avi: pendingList) {
      if (avi.isNewlyCreated() || avi.isUpdatedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = rivTaProfilService.save(avi); // PERSIST the Updated elements.
        pvCache.rivTaProfil.put((int) avi.getId(), avi);
      }
    }
  }
  private void addUpdateTjanstekontrakt(PublishedVersionCache pvCache, Long newPvId, List<Tjanstekontrakt> pendingList) {
    for (Tjanstekontrakt avi: pendingList) {
      if (avi.isNewlyCreated() || avi.isUpdatedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = tjanstekontraktService.save(avi); // PERSIST the Updated elements.
        pvCache.tjanstekontrakt.put((int) avi.getId(), avi);
      }
    }
  }
  private void addUpdateTjanstekomponent(PublishedVersionCache pvCache, Long newPvId, List<Tjanstekomponent> pendingList) {
    for (Tjanstekomponent avi: pendingList) {
      if (avi.isNewlyCreated() || avi.isUpdatedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = tjanstekomponentService.save(avi); // PERSIST the Updated elements.
        pvCache.tjanstekomponent.put((int) avi.getId(), avi);
      }
    }
  }
  private void addUpdateLogiskAdress(PublishedVersionCache pvCache, Long newPvId, List<LogiskAdress> pendingList) {
    for (LogiskAdress avi: pendingList) {
      if (avi.isNewlyCreated() || avi.isUpdatedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = logiskAdressService.save(avi); // PERSIST the Updated elements.
        pvCache.logiskAdress.put((int) avi.getId(), avi);
      }
    }
  }
  private void addUpdateAnropsAdress(PublishedVersionCache pvCache, Long newPvId, List<AnropsAdress> pendingList) {
    for (AnropsAdress avi: pendingList) {
      if (avi.isNewlyCreated() || avi.isUpdatedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = anropsAdressService.save(avi); // PERSIST the Updated elements.
        pvCache.anropsAdress.put((int) avi.getId(), avi);
      }
    }
  }
  private void addUpdateVagval(PublishedVersionCache pvCache, Long newPvId, List<Vagval> pendingList) {
    for (Vagval avi: pendingList) {
      if (avi.isNewlyCreated() || avi.isUpdatedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = vagvalService.save(avi); // PERSIST the Updated elements.
        pvCache.vagval.put((int) avi.getId(), avi);
      }
    }
  }
  private void addUpdateAnropsbehorighet(PublishedVersionCache pvCache, Long newPvId, List<Anropsbehorighet> pendingList) {
    for (Anropsbehorighet avi: pendingList) {
      if (avi.isNewlyCreated() || avi.isUpdatedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = anropsBehorighetService.save(avi); // PERSIST the Updated elements.
        pvCache.anropsbehorighet.put((int) avi.getId(), avi);
      }
    }
  }
  private void addUpdateFilter(PublishedVersionCache pvCache, Long newPvId, List<Filter> pendingList) {
    for (Filter avi: pendingList) {
      if (avi.isNewlyCreated() || avi.isUpdatedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = filterService.save(avi); // PERSIST the Updated elements.
        pvCache.filter.put((int) avi.getId(), avi);
      }
    }
  }
  private void addUpdateFiltercategorizations(PublishedVersionCache pvCache, Long newPvId, List<Filtercategorization> pendingList) {
    for (Filtercategorization avi: pendingList) {
      if (avi.isNewlyCreated() || avi.isUpdatedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = filterCategorizationService.save(avi); // PERSIST the Updated elements.
        pvCache.filtercategorization.put((int) avi.getId(), avi);
      }
    }
  }

  // Patterned Add-Update routines.
  //   TODO: Make Generics?
  //   These might be possible to generalize through some T-type pattern or reflection,
  //   but as some of the lists and containers are types sch optimization has been pushed into the future.
  private void deleteRivTaProfil(PublishedVersionCache pvCache, Long newPvId, List<RivTaProfil> pendingList) {
    for (RivTaProfil avi: pendingList) {
      if (avi.isDeletedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = rivTaProfilService.save(avi); // PERSIST the Updated elements.
        pvCache.rivTaProfil.remove((int) avi.getId());
      }
    }
  }
  private void deleteTjanstekontrakt(PublishedVersionCache pvCache, Long newPvId, List<Tjanstekontrakt> pendingList) {
    for (Tjanstekontrakt avi: pendingList) {
      if (avi.isDeletedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = tjanstekontraktService.save(avi); // PERSIST the Updated elements.
        pvCache.tjanstekontrakt.remove((int) avi.getId());
      }
    }
  }
  private void deleteTjanstekomponent(PublishedVersionCache pvCache, Long newPvId, List<Tjanstekomponent> pendingList) {
    for (Tjanstekomponent avi: pendingList) {
      if (avi.isDeletedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = tjanstekomponentService.save(avi); // PERSIST the Updated elements.
        pvCache.tjanstekomponent.remove((int) avi.getId());
      }
    }
  }
  private void deleteLogiskAdress(PublishedVersionCache pvCache, Long newPvId, List<LogiskAdress> pendingList) {
    for (LogiskAdress avi: pendingList) {
      if (avi.isDeletedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = logiskAdressService.save(avi); // PERSIST the Updated elements.
        pvCache.logiskAdress.remove((int) avi.getId());
      }
    }
  }
  private void deleteAnropsAdress(PublishedVersionCache pvCache, Long newPvId, List<AnropsAdress> pendingList) {
    for (AnropsAdress avi: pendingList) {
      if (avi.isDeletedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = anropsAdressService.save(avi); // PERSIST the Updated elements.
        pvCache.anropsAdress.remove((int) avi.getId());
      }
    }
  }
  private void deleteVagval(PublishedVersionCache pvCache, Long newPvId, List<Vagval> pendingList) {
    for (Vagval avi: pendingList) {
      if (avi.isDeletedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = vagvalService.save(avi); // PERSIST the Updated elements.
        pvCache.vagval.remove((int) avi.getId());
      }
    }
  }
  private void deleteAnropsbehorighet(PublishedVersionCache pvCache, Long newPvId, List<Anropsbehorighet> pendingList) {
    for (Anropsbehorighet avi: pendingList) {
      if (avi.isDeletedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = anropsBehorighetService.save(avi); // PERSIST the Updated elements.
        pvCache.anropsbehorighet.remove((int) avi.getId());
      }
    }
  }
  private void deleteFilter(PublishedVersionCache pvCache, Long newPvId, List<Filter> pendingList) {
    for (Filter avi: pendingList) {
      if (avi.isDeletedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = filterService.save(avi); // PERSIST the Updated elements.
        pvCache.filter.remove((int) avi.getId());
      }
    }
  }
  private void deleteFiltercategorizations(PublishedVersionCache pvCache, Long newPvId, List<Filtercategorization> pendingList) {
    for (Filtercategorization avi: pendingList) {
      if (avi.isDeletedAfterPublishedVersion()) {
        addUpdateCommonalities(avi, newPvId);
        avi = filterCategorizationService.save(avi); // PERSIST the Updated elements.
        pvCache.filtercategorization.remove((int) avi.getId());
      }
    }
  }

  /**
   * These lines were shared between all the add update and delete functions.
   */
  private void addUpdateCommonalities(AbstractVersionInfo avi, Long newPvId) {
    avi.setPubVersion(newPvId.toString());
    avi.setUpdatedBy(null);
    avi.setUpdatedTime(null);
  }
}
