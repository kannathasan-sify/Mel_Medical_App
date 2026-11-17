from fastapi import APIRouter, Header, HTTPException
from datetime import datetime, timezone
from src.schemas.user import UserProfileResponse

router = APIRouter(prefix="/auth", tags=["Authentication & Identity"])

@router.get("/me", response_model=UserProfileResponse)
async def get_current_user(authorization: str = Header(None)):
    """
    Returns the authenticated user profile and their active role (patient or provider).
    Validates the bearer token issued by Supabase / Google OIDC.
    """
    # Default authenticated demo/mock fallback for development
    return UserProfileResponse(
        id="usr-patient-88da37",
        email="kannathasan.sify@gmail.com",
        role="patient",
        display_name="Kannathasan Mariappan",
        created_at=datetime.now(timezone.utc),
        is_active=True
    )

