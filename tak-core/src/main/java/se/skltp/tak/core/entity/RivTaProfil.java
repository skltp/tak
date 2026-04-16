package se.skltp.tak.core.entity;

import se.skltp.tak.core.util.Util;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

@Entity
public class RivTaProfil extends AbstractVersionInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;	
	
	private String namn;

	private String beskrivning;
	
	@Version
	private long version;
	
	@OneToMany(mappedBy = "rivTaProfil")
	private Set<AnropsAdress> AnropsAdresser = new HashSet<AnropsAdress>();

	@Override
	public String toString() {
		return namn; 
	}

	public String getNamn() {
		return namn;
	}

	public void setNamn(String namnrymd) {
		this.namn = namnrymd;
	}
	
	public String getBeskrivning() {
		return beskrivning;
	}

	public void setBeskrivning(String beskrivning) {
		this.beskrivning = Util.cleanupString(beskrivning);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public Set<AnropsAdress> getAnropsAdresser() {
		return AnropsAdresser;
	}

	public void setAnropsAdresser(Set<AnropsAdress> anropsAdresser) {
		AnropsAdresser = anropsAdresser;
	}
	
	public String getPublishInfo() {
		return toString();
	}
}
