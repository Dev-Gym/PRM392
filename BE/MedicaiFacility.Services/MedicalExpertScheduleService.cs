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

        public string AddMedicalExpertSchedule(MedicalExpertSchedule schedule)
        {
            var exsitingSchedule = _repository.GetAll().Where(x => x.IsActive==true && x.ExpertId == schedule.ExpertId && x.StartDate.Date == schedule.StartDate.Date);
            if (exsitingSchedule.Any()) return "đã tồn tại lịch này rồi";
            var result =  _repository.AddMedicalExpertSchedule(schedule);
            if (result == null) return "Tạo thất bại";
            return "Tạo thành công";
        }

        public void DeleteSchedulesByExpertId(int expertId)
        {
            _repository.DeleteSchedulesByExpertId(expertId);
        }

  
        public MedicalExpertSchedule UpdateMedicalExpertSchedule(MedicalExpertSchedule schedule)
        {
            return _repository.UpdateMedicalExpertSchedule(schedule);
        }

        public string IsValid(int id, DateTime startTime, DateTime EndTime)
        {
            var item = _repository.GetAll().FirstOrDefault(x => x.ScheduleId == id);
            if (item == null || item.IsActive == false)
            {
                return "Không tìm thấy lịch làm việc hoặc bác sĩ đã ngưng hoạt động.";
            }

            // Kiểm tra thời gian kết thúc phải lớn hơn bắt đầu
            if (EndTime <= startTime)
            {
                return "End time phải lớn hơn start time.";
            }

            // Kiểm tra thời lượng tối thiểu là 30 phút
            if ((EndTime - startTime).TotalMinutes < 30)
            {
                return "Khoảng thời gian cuộc hẹn phải ít nhất là 30 phút.";
            }

            // Kiểm tra startTime nằm trong giờ làm việc bác sĩ
            if (startTime< item.StartDate || startTime > item.EndDate)
            {
                return "Thời gian bắt đầu cuộc hẹn phải nằm trong giờ làm việc của bác sĩ.";
            }

            // Kiểm tra endTime cũng phải nằm trong giờ bác sĩ
            if (EndTime < item.StartDate || EndTime > item.EndDate)
            {
                return "Thời gian kết thúc cuộc hẹn phải nằm trong giờ làm việc của bác sĩ.";
            }

            return "true";
        }
    }
}