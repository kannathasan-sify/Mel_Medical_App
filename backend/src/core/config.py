import os
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    PROJECT_NAME: str = "MEL - Medical Empowerment Liaison API"
    VERSION: str = "1.0.0"
    API_V1_PREFIX: str = "/api/v1"
    
    # Supabase Configuration
    SUPABASE_URL: str = "https://ptuqsmureupubeiafihy.supabase.co"
    SUPABASE_ANON_KEY: str = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB0dXFzbXVyZXVwdWJlaWFmaWh5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTQ1NDQwMTUsImV4cCI6MjExMDEyMDAxNX0.HnPPwBsOnuxtXAR0q61tYX40WrqSXflPPp4TNffk8Rk"
    SUPABASE_SERVICE_ROLE_KEY: str = "" # Optional server-side master key
    
    # AI Engine Configuration
    OPENAI_API_KEY: str = os.getenv("OPENAI_API_KEY", "")
    OPENAI_MODEL: str = "gpt-4o"
    
    # Security
    ALLOWED_ORIGINS: list[str] = ["*"]
    
    class Config:
        env_file = ".env"
        extra = "ignore"

settings = Settings()

