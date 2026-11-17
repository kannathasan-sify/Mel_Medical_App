from pydantic import BaseModel, EmailStr
from typing import Optional, List
from datetime import datetime

class UserProfileResponse(BaseModel):
    id: str
    email: str
    role: str # "patient" or "provider"
    display_name: Optional[str] = None
    created_at: datetime
    is_active: bool = True

class PatientProfileResponse(BaseModel):
    id: str
    user_id: str
    name: str
    preferred_name: Optional[str] = None
    primary_language: str = "English"
    communication_style: str = "Standard"
    reading_level_preference: str = "8th_grade"
    created_at: datetime

class ProviderProfileResponse(BaseModel):
    id: str
    user_id: str
    full_name: str
    specialty: str
    organization_name: Optional[str] = None
    license_number: Optional[str] = None
    verified: bool = True

