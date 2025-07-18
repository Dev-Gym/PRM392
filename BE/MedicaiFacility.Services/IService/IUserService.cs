using MedicaiFacility.BusinessObject;
using Microsoft.AspNetCore.Http;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace MedicaiFacility.Service.IService
{
    public interface IUserService
    {
        User FindById(int id);
        User SignIn(string identifier, string password);
        void Add(User user);
        IEnumerable<User> GetAllUsers();
        void UpdateUser(User user);
        Task UpdateUserAndSessionAsync(User user, HttpContext httpContext); // Thêm phương thức này
        User FindByEmail(string email);
        User FindByPhoneNumber(string phoneNumber);
        User IsExistEmail(string email);
        User ValidatePassword(string email, string password);
        void ChangePassword(string email, string newPassword);
        User GetUserByPatientId(int patientId);
        IEnumerable<User> GetAllExpertMedical();
        IEnumerable<User> GetAllPatient();
    }
}