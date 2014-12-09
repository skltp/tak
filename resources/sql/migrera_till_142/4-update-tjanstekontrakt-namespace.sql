# Uppdaterar namnrymden i Tjanstekontrakt se Jira https://skl-tp.atlassian.net/browse/SKLTP-581
#
# tjänsteinteraktion namespace: urn:riv:crm:scheduling:GetSubjectOfCareSchedule:1:rivtabp21
# tjänstekontrakt namespace: urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1
#
# Korrekt namnrymd är tjänstekontrakt namespace.

UPDATE takv2.Tjanstekontrakt set namnrymd = CONCAT(SUBSTRING(namnrymd, 1, CHAR_LENGTH(namnrymd) - 12), 'Responder:', substring(namnrymd,-11, 1));
