using Microsoft.AspNetCore.Mvc;
using MedicaiFacility.Services;
using MedicaiFacility.BusinessObject;
using MedicaiFacility.Service.IService;

namespace MedicalFacilityAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class MedicalHistoryController : ControllerBase
    {
        private readonly IMedicalHistoryService _medicalHistoryService;
        private readonly IAppointmentService _appointmentService;   
        public MedicalHistoryController(IMedicalHistoryService medicalHistoryService, IAppointmentService appointmentService)
        {
            _medicalHistoryService = medicalHistoryService;
            _appointmentService = appointmentService;
        }

        // Lấy lịch sử khám bệnh của user
        [HttpGet]
        public ActionResult<List<MedicalHistory>> GetMyMedicalHistory([FromQuery] int userId)
        {
            var histories = _medicalHistoryService.GetAllByUserId(userId).OrderByDescending(x=>x.HistoryId);
            return Ok(histories);
        }
        [HttpGet("{medicalHistoryId:int}")]
        public ActionResult<MedicalHistory> GetMedicalHistoryById(int medicalHistoryId) {
            var item = _medicalHistoryService.GetById(medicalHistoryId);
            return Ok(item);
        }
        [HttpPost]
        public ActionResult<MedicalHistory> Create([FromBody] MedicalHistoryrequest req) {
            var existing = _medicalHistoryService.ExistingMedicalHistory(req.AppointmentId);
            if (existing != null) { 
                return BadRequest(new 
                { Message = $"medical history is existing with medicalHistoryId:{existing.HistoryId}" +
                $" - appointment :{existing.AppointmentId}" });
            }
            var newHistory = new MedicalHistory {
                AppointmentId = req.AppointmentId,
                Description = req.Description,
                Status = req.Status,
                Payed = true,
                CreatedAt = DateTime.Now,   
                UpdatedAt = DateTime.Now,
            };
            var result = _medicalHistoryService.Create(newHistory);
            return Ok(newHistory);
        }
        [HttpPut("{MedicalHistoryId:int}")]
        public ActionResult<MedicalHistory> Create(int MedicalHistoryId , [FromBody] MedicalHistoryrequest req)
        {
            var existing = _medicalHistoryService.ExistingMedicalHistory(req.AppointmentId);
            if ( existing != null&& existing.HistoryId != MedicalHistoryId)
            {
                return BadRequest(new
                {
                    Message = $"medical history is existing with medicalHistoryId:{existing.HistoryId}" +
                $" - appointment :{existing.AppointmentId}"
                });
            }
            var item = _medicalHistoryService.GetById(MedicalHistoryId);

            item.AppointmentId = req.AppointmentId;
            item.Description = req.Description;
            item.Status = req.Status;
            item.Payed = true;
            item.UpdatedAt = DateTime.Now;
         
            var result = _medicalHistoryService.Update(item);
            return Ok(item);
        }
        [HttpPut("delete/{MedicalHistoryId:int}")]
        public ActionResult<MedicalHistory> Delete(int MedicalHistoryId)
        {
            var item = _medicalHistoryService.GetById(MedicalHistoryId);
            item.Status = "IsDeleted";
            var result = _medicalHistoryService.Update(item);
            return Ok(item);
        }

        [HttpPut("processing/{MedicalHistoryId:int}")]
        public ActionResult<MedicalHistory> Processing(int MedicalHistoryId)
        {
            var item = _medicalHistoryService.GetById(MedicalHistoryId);
            item.Status = "Processing";
            var result = _medicalHistoryService.Update(item);
            return Ok(item);
        }

        [HttpPut("cancelled/{MedicalHistoryId:int}")]
        public ActionResult<MedicalHistory> Cancelled(int MedicalHistoryId)
        {
           
            var item = _medicalHistoryService.GetById(MedicalHistoryId);
            item.Status = "Cancelled";
           
            var result = _medicalHistoryService.Update(item);
            var appointment = _appointmentService.GetById((int)item.AppointmentId);
            if (appointment!=null&& appointment.Status== "Confirmed") {
                appointment.Status = "Cancelled";
                _appointmentService.Update(appointment);
            }
            return Ok(item);
        }
        [HttpPut("completed/{MedicalHistoryId:int}")]
        public ActionResult<MedicalHistory> Completed(int MedicalHistoryId, [FromBody] MedicalHistoryConfirmrequest req)
        {

            var item = _medicalHistoryService.GetById(MedicalHistoryId);
            item.Status = "Completed";
            item.Description = req.Description;
            item.Payed = true;
            var result = _medicalHistoryService.Update(item);
            var appointment = _appointmentService.GetById((int)item.AppointmentId);
            if (appointment != null && appointment.Status == "Confirmed")
            {
                appointment.Status = "Completed";
                _appointmentService.Update(appointment);
            }
            return Ok(item);
        }
    }
    public class MedicalHistoryrequest {
        public int AppointmentId { get; set; }

        public string Description { get; set; }

        public string Status { get; set; }

    }
    public class MedicalHistoryConfirmrequest
    {

        public string Description { get; set; }


    }

} 