package se.skltp.tak.web.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

@Entity
public class Anvandare implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	@NotBlank
	@Column(unique = true, nullable = false)
    private String anvandarnamn;

	@Column(name = "losenord_hash")
	private String losenordHash;

	@Transient
	private String losenord;

	private Boolean administrator;

	@Version
	private long version;

	@Override
	public String toString() {
		return anvandarnamn;
	}

	public String getLosenordHash() {
		return losenordHash;
	}

	public void setLosenordHash(String losenordHash) {
		this.losenordHash = losenordHash;
	}

	public String getAnvandarnamn() {
		return anvandarnamn;
	}

	public void setAnvandarnamn(String anvandarnamn) {
		this.anvandarnamn = anvandarnamn;
	}

	public boolean getAdministrator() {
		return administrator != null && administrator;
	}

	public void setAdministrator(Boolean administrator) {
		this.administrator = administrator;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLosenord() {
		return losenord;
	}

	public void setLosenord(String losenord) {
		this.losenord = losenord;
	}
}
