using MedicaiFacility.BusinessObject;
using System.Collections.Generic;

namespace MedicaiFacility.DataAccess.IRepostory
{
    public interface IUserRepository
    {
        User FindById(int id);
        User FindByEmail(string email);

        void Add(User user);
        User FindByPhoneNumber(string phoneNumber);
        User ValidateLogin(string identifier, string password);
     
        IEnumerable<User> GetAllUsers();
        void UpdateUser(User user); // Thêm phương thức này
        User IsExistEmail(string email);
        bool ValidatePassword(string email, string password);
        void ChangePassword(string email, string newPassword);
        User GetUserByPatientId(int patientId);

    }
}