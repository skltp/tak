package se.skltp.tak.core.entity

constraints = {
	servicedomain(blank:false, nullable:false, maxSize: 255, unique: ['anropsbehorighet', 'deleted'], validator: { val, obj ->
		
		if (val?.startsWith(" ")) {
			return 'invalid.leadingspace'
		}
		if (val?.startsWith("\t")) {
			return 'invalid.leadingtab'
		}
		if (val?.endsWith(" ")) {
			return 'invalid.trailingspace'
		}
		if (val?.endsWith("\t")) {
			return 'invalid.trailingtab'
		}
        
        if (val) {
			boolean foundWhiteSpace = false
			
			for (char c : val.value) {
				if (c.isWhitespace()) {
					foundWhiteSpace = true
					break
				}
			}
			
			if (foundWhiteSpace) { return 'invalid.space.newline' }
		}
        
        return true
    })
	
	anropsbehorighet(nullable: false)
}
