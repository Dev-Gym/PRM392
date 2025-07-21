using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess.IRepostory;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;

namespace MedicaiFacility.DataAccess
{
    public class RatingsAndFeedbackRepository : IRatingsAndFeedbackRepository
    {
        private readonly AppDbContext _context;

        public RatingsAndFeedbackRepository(AppDbContext context)
        {
            _context = context;
        }

        public void Add(RatingsAndFeedback ratingsAndFeedback)
        {
            _context.RatingsAndFeedbacks.Add(ratingsAndFeedback);
            _context.SaveChanges();
        }

        public void Delete(int id)
        {
            var item = _context.RatingsAndFeedbacks.FirstOrDefault(x => x.FeedbackId == id);
            if (item != null)
            {
                _context.RatingsAndFeedbacks.Remove(item);
                _context.SaveChanges();
            }
        }

        public List<RatingsAndFeedback> GetAll()
        {
            return _context.RatingsAndFeedbacks
                .Include(r => r.MedicalHistory) // Sửa lỗi Include
                .ToList();
        }
		public List<RatingsAndFeedback> GetAllByExpertId(int expertId)
		{
			return _context.RatingsAndFeedbacks
				.Include(r => r.MedicalHistory).ThenInclude(x=>x.Appointment)
                .ThenInclude(X=>X.Patient).ThenInclude(X=>X.PatientNavigation).AsEnumerable()
                
                .Where(x=>x.MedicalHistory.Appointment.ExpertId==expertId) 
				.ToList();
		}
		public RatingsAndFeedback FindById(int id)
        {
            return _context.RatingsAndFeedbacks
                .Include(r => r.MedicalHistory) 
                .FirstOrDefault(r => r.FeedbackId == id);
        }

        public (List<RatingsAndFeedback>, int totalItem) FindAllWithPagination(int pg, int pageSize)
        {
            var list = _context.RatingsAndFeedbacks
                .Include(r => r.MedicalHistory) 
                .ToList();

            var total = list.Count();
            int skip = (pg - 1) * pageSize;
            var data = list.Skip(skip).Take(pageSize).ToList();

            return (data, total);
        }

        public void Save(RatingsAndFeedback ratingsAndFeedback)
        {
            _context.RatingsAndFeedbacks.Add(ratingsAndFeedback);
            _context.SaveChanges();
        }

        public void Update(RatingsAndFeedback ratingsAndFeedback)
        {
            _context.RatingsAndFeedbacks.Update(ratingsAndFeedback);
            _context.SaveChanges();
        }

        public RatingsAndFeedback findByHisId(int id)
        {
          return  _context.RatingsAndFeedbacks.FirstOrDefault(x => x.MedicalHistoryId == id);
        }
    }
}