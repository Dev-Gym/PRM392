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
        public MedicalHistoryController(IMedicalHistoryService medicalHistoryService)
        {
            _medicalHistoryService = medicalHistoryService;
        }

        // Lấy lịch sử khám bệnh của user
        [HttpGet("{userId:int}")]
        public ActionResult<List<MedicalHistory>> GetMyMedicalHistory(int userId)
        {
            var histories = _medicalHistoryService.GetALlPagainationsByPatientId(pg:0, pageSize:0, userId).list;
            return Ok(histories);
        }
    }
} 