using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess.IRepostory;
using MedicaiFacility.Service.IService;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Http;
using System.Collections.Generic;
using System.Security.Claims;
using System.Threading.Tasks;

namespace MedicaiFacility.Service
{
    public class UserService : IUserService
    {
        private readonly IUserRepository _userRepository;

        public UserService(IUserRepository userRepository)
        {
            _userRepository = userRepository;
        }

        public User FindById(int id)
        {
            return _userRepository.FindById(id);
        }

        public User SignIn(string identifier, string password)
        {
            return _userRepository.ValidateLogin(identifier, password);
        }

        public void Add(User user)
        {
            _userRepository.Add(user);
        }

        public IEnumerable<User> GetAllUsers()
        {
            return _userRepository.GetAllUsers();
        }

        public void UpdateUser(User user)
        {
            _userRepository.UpdateUser(user);
        }

        public async Task UpdateUserAndSessionAsync(User user, HttpContext httpContext)
        {
            _userRepository.UpdateUser(user);

            var identity = (ClaimsIdentity)httpContext.User.Identity;

            var existingNameClaim = identity.FindFirst(ClaimTypes.Name);
            if (existingNameClaim != null)
            {
                identity.RemoveClaim(existingNameClaim);
            }
            identity.AddClaim(new Claim(ClaimTypes.Name, user.FullName ?? string.Empty));

            var existingEmailClaim = identity.FindFirst(ClaimTypes.Email);
            if (existingEmailClaim != null)
            {
                identity.RemoveClaim(existingEmailClaim);
            }
            identity.AddClaim(new Claim(ClaimTypes.Email, user.Email ?? string.Empty));

            await httpContext.SignInAsync(
                CookieAuthenticationDefaults.AuthenticationScheme,
                new ClaimsPrincipal(identity),
                new AuthenticationProperties { IsPersistent = true }
            );
        }

        public User FindByEmail(string email)
        {
            return _userRepository.FindByEmail(email);
        }

        public User IsExistEmail(string email)
        {
            return _userRepository.IsExistEmail(email);
        }

        public User ValidatePassword(string email, string password)
        {
            var user = _userRepository.FindByEmail(email);
            if (user == null)
            {
                return null;
            }
            if (user.Password != password) return null;
            return user;
        }

        public void ChangePassword(string email, string newPassword)
        {
            _userRepository.ChangePassword(email, newPassword);
        }

        public User FindByPhoneNumber(string phoneNumber)
        {
           return _userRepository.FindByPhoneNumber(phoneNumber);
        }
        public User GetUserByPatientId(int patientId)
        {
            return _userRepository.GetUserByPatientId(patientId);
        }

        public IEnumerable<User> GetAllExpertMedical()
        {
            return _userRepository.GetAllExpertMedical();
        }

        public IEnumerable<User> GetAllPatient()
        {
          return _userRepository.GetAllPatient();
        }
    }
}