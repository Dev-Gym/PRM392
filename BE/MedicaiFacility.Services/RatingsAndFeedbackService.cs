using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess;
using MedicaiFacility.DataAccess.IRepostory;
using System.Collections.Generic;
using MedicaiFacility.Services;
using MedicaiFacility.Service.IService;

namespace MedicaiFacility.Services
{
    public class RatingsAndFeedbackService : IRatingsAndFeedbackService
    {
        private readonly IRatingsAndFeedbackRepository _repository;

        public RatingsAndFeedbackService(IRatingsAndFeedbackRepository repository)
        {
            _repository = repository;
        }

        public async Task<RatingsAndFeedback?> GetFeedbackAsync(int id)
        {
            return await Task.FromResult(_repository.FindById(id));
        }

        public List<RatingsAndFeedback> GetAll()
        {
            return _repository.GetAll();
        }

        public RatingsAndFeedback FindById(int id)
        {
            return _repository.FindById(id);
        }

        public void Save(RatingsAndFeedback feedback)
        {
            _repository.Save(feedback);
        }

        public void Udpate(RatingsAndFeedback feedback)
        {
            _repository.Update(feedback);
        }

        public void Add(RatingsAndFeedback feedback)
        {
            _repository.Add(feedback);
        }


        public void deleteById(int id)
        {
            _repository.Delete(id);
        }

        public async Task<(List<RatingsAndFeedback>, int totalItem)> FindAllWithPaginationAsync(int pg, int pageSize)
        {
            return await Task.FromResult(_repository.FindAllWithPagination(pg, pageSize));
        }

        public RatingsAndFeedback findByHisId(int id)
        {
         return  _repository.findByHisId(id);
        }

		public List<RatingsAndFeedback> GetAllByExpertId(int expertId)
		{
            return _repository.GetAllByExpertId(expertId);
		}
	}
}
