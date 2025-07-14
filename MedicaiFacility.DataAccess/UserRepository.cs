using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess.IRepostory;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;

namespace MedicaiFacility.DataAccess
{
    public class UserRepository : IUserRepository
    {
        private readonly AppDbContext _context;

        public UserRepository(AppDbContext context)
        {
            _context = context ?? throw new ArgumentNullException(nameof(context));
        }

        public User FindById(int id) => _context.Users.FirstOrDefault(x => x.UserId == id);

        public User FindByEmail(string email) => _context.Users.FirstOrDefault(x => x.Email == email);

        public User FindByPhoneNumber(string phoneNumber) => _context.Users.FirstOrDefault(x => x.PhoneNumber == phoneNumber);

        public void Add(User user)
        {
            _context.Users.Add(user);
            _context.SaveChanges();
        }

        public User ValidateLogin(string identifier, string password)
        {
            var user = FindByEmail(identifier);
            if (user == null)
            {
                user = FindByPhoneNumber(identifier);
            }

            if (user != null && user.Password == password && user.Status == true)
            {
                return user;
            }
            return null;
        }

       

        public IEnumerable<User> GetAllUsers()
        {
            return _context.Users.OrderByDescending(x=>x.UserId).Include(x => x.MedicalExpert).ThenInclude(x => x.Facility).Include(x=>x.MedicalExpert).ThenInclude(x=>x.MedicalExpertSchedules).ToList();
        }

        public void UpdateUser(User user)
        {
            var existingUser = FindById(user.UserId);
            if (existingUser == null)
            {
                throw new Exception("User not found!");
            }

            existingUser.FullName = user.FullName;
            existingUser.Email = user.Email;
            existingUser.PhoneNumber = user.PhoneNumber;
            existingUser.UserType = user.UserType;
            existingUser.BankAccount = user.BankAccount;
            existingUser.Status = user.Status;
            existingUser.UpdatedAt = DateTime.Now;

            _context.SaveChanges();
        }

        public User IsExistEmail(string email)
        {
            return _context.Users.FirstOrDefault(x => x.Email.Equals(email));
        }

        public bool ValidatePassword(string email, string password)
        {
            var user = FindByEmail(email);
            if (user == null)
            {
                return false;
            }
            return user.Password == password; 
        }

        public void ChangePassword(string email, string newPassword)
        {
            var user = FindByEmail(email);
            if (user != null)
            {
                user.Password = newPassword;
                user.UpdatedAt = DateTime.Now;
                _context.Users.Update(user);
                _context.SaveChanges();
            }
        }
        public User GetUserByPatientId(int patientId)
        {
            var patient = _context.Patients.FirstOrDefault(p => p.PatientId == patientId);
            if (patient == null)
            {
                return null;
            }
            return _context.Users.FirstOrDefault(u => u.UserId == patient.PatientId);
        }
    }
}