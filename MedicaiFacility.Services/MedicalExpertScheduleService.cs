using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess;
using MedicaiFacility.DataAccess.IRepostory;
using MedicaiFacility.Repository;
using MedicaiFacility.Service.IService;
using System;
using System.Collections.Generic;
using System.Linq;

namespace MedicaiFacility.Service
{
    public class MedicalExpertScheduleService : IMedicalExpertScheduleService
    {
        private readonly IMedicalExpertScheduleRepository _repository;

        public MedicalExpertScheduleService(IMedicalExpertScheduleRepository repository)
        {
            _repository = repository ?? throw new ArgumentNullException(nameof(repository));
        }

        public List<MedicalExpertSchedule> GetSchedulesByExpertId(int expertId)
        {
            return _repository.GetSchedulesByExpertId(expertId).ToList();
        }

        public void AddMedicalExpertSchedule(MedicalExpertSchedule schedule)
        {
            _repository.AddMedicalExpertSchedule(schedule);
        }

        public void DeleteSchedulesByExpertId(int expertId)
        {
            _repository.DeleteSchedulesByExpertId(expertId);
        }
    }
}