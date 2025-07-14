using Microsoft.AspNetCore.Mvc;
using MedicaiFacility.Services;
using MedicaiFacility.BusinessObject;
using MedicaiFacility.Service.IService;

namespace MedicalFacilityAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly IUserService _userService;
        public AuthController(IUserService userService)
        {
            _userService = userService;
        }

        [HttpPost("login")]
        public IActionResult Login([FromBody] LoginRequest req)
        {
            var user = _userService.ValidatePassword(req.Email, req.Password);
            if (user == null || user.Status == false)
                return Unauthorized(new { message = "Invalid credentials" });

            return Ok(new
            {
                user.UserId,
                user.FullName,
                user.Email,
                user.UserType
            });
        }
    }

    public record LoginRequest (string Email, string Password);
    
} 